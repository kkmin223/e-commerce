package kr.hhplus.be.server.lock.aop;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // Lock 생성
        String[] lockKeys = resolveKeys(joinPoint, distributedLock.keys());

        RLock rLock = createLock(lockKeys);

        try {
            boolean available = rLock.tryLock(
                distributedLock.waitTime(),
                distributedLock.leaseTime(),
                distributedLock.timeUnit()
            );

            if (!available) {
                throw new BusinessLogicException(ErrorCode.ACQUIRED_LOCK_FAIL);
            }
            log.info("분산락 획득: {}", signature.getName());

            // 비즈니스 로직 실행
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
                log.info("분산락 해제: {}", signature.getName());
            } catch (IllegalMonitorStateException e) {
                log.info("분산락 이미 해제됨: {}", signature.getName());
            }
        }
    }

    private RLock createLock(String[] keys) {
        List<RLock> locks = Arrays.stream(keys)
            .map((key) -> redissonClient.getLock(REDISSON_LOCK_PREFIX + key))
            .toList();

        return 1 < locks.size() ?
            redissonClient.getMultiLock(locks.toArray(new RLock[0]))
            : locks.get(0);
    }

    private String[] resolveKeys(ProceedingJoinPoint joinPoint, String[] keyExpressions) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        return Arrays.stream(keyExpressions)
            .flatMap(expr -> parseExpression(method, args, expr).stream())
            .distinct()
            .toArray(String[]::new);
    }

    private List<String> parseExpression(Method method, Object[] args, String expression) {
        EvaluationContext context = new MethodBasedEvaluationContext(
            null, method, args, parameterNameDiscoverer
        );

        try {
            Object value = expressionParser.parseExpression(expression).getValue(context);
            return convertToStringList(value);
        } catch (EvaluationException e) {
            throw new IllegalArgumentException("SpEL 파싱 실패: " + expression, e);
        }
    }

    private List<String> convertToStringList(Object value) {
        if (value instanceof String[]) {
            return Arrays.asList((String[]) value);
        } else if (value instanceof Collection<?> collection) {
            return collection.stream().map(Object::toString).toList();
        } else if (value != null) {
            return List.of(value.toString());
        }
        return List.of();
    }
}
