package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class CouponFacadeConcurrencyTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Test
    void 동시에_20명이_쿠폰을_요청한다() throws InterruptedException {
        // Given
        final int INITIAL_QUANTITY = 20;
        final int THREAD_COUNT = 21;  // 초과 요청 1건 포함

        Coupon coupon = couponRepository.save(new TestCoupon("동시성 테스트 쿠폰", INITIAL_QUANTITY));
        List<User> users = createTestUsers(THREAD_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(THREAD_COUNT);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        // When
        for (User user : users) {
            executor.submit(() -> {
                try {
                    startLatch.await();  // 모든 스레드 동시 시작
                    couponFacade.IssueCoupon(CouponCriteria.Issue.of(user.getId(), coupon.getId()));
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();  // 동시 실행 개시
        finishLatch.await(10, TimeUnit.SECONDS);  // 최대 10초 대기
        executor.shutdown();

        // Then
        Coupon updatedCoupon = couponRepository.getCoupon(coupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getRemainingQuantity()).isZero();

        int issuedCount = couponItemRepository.countByCouponId(coupon.getId());
        assertThat(issuedCount).isEqualTo(INITIAL_QUANTITY);

        assertThat(exceptions)
            .hasSize(THREAD_COUNT - INITIAL_QUANTITY)
            .allMatch(e -> e instanceof BusinessLogicException
                || e instanceof OptimisticLockingFailureException);
    }

    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(userRepository.save(User.of(1_000)));
        }
        return users;
    }
}
