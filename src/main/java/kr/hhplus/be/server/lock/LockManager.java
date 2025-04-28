package kr.hhplus.be.server.lock;


import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class LockManager {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(Supplier<T> executeFunction
        , List<String> lockKeys
        , long waitTime
        , long leaseTime
        , TimeUnit unit) {

        // 1. Lock 생성
        RLock lock = createLock(lockKeys);

        try {
            // 2. 락 획득
            boolean acquired = lock.tryLock(waitTime, leaseTime, unit);

            if (!acquired) {
                throw new BusinessLogicException(ErrorCode.ACQUIRED_LOCK_FAIL);
            }

            // 3. 메소드 실행
            return executeFunction.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessLogicException(ErrorCode.ACQUIRED_LOCK_FAIL);
        } finally {
            // 4. 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private RLock createLock(List<String> lockKeys) {

        // 1. lock key 정렬(데드락 방지)
        Collections.sort(lockKeys);

        // 2. Lock 생성
        List<RLock> locks = lockKeys
            .stream()
            .map(redissonClient::getLock)
            .toList();

        // 3. 2개 이상이면 MultiLock, 1개이면 단일 Lock
        return 1 < locks.size()
            ? redissonClient.getMultiLock(locks.toArray(new RLock[0])) : locks.get(0);

    }

}
