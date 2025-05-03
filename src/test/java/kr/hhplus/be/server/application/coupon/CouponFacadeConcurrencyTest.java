package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE coupon");
        jdbcTemplate.execute("TRUNCATE TABLE coupon_item");
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @Test
    void 동시에_2개수량을가진_쿠폰에_3명이_쿠폰을_요청한다() throws InterruptedException {
        // Given
        final int INITIAL_QUANTITY = 2;
        final int THREAD_COUNT = 3;

        Coupon coupon = couponRepository.save(new TestCoupon("동시성 테스트 쿠폰", INITIAL_QUANTITY));
        List<User> users = createTestUsers(THREAD_COUNT);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (User user : users) {
            executor.submit(() -> {
                try {
                    couponFacade.IssueCoupon(CouponCriteria.Issue.of(user.getId(), coupon.getId()));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(successCount.get()).isEqualTo(INITIAL_QUANTITY);
        assertThat(failCount.get()).isEqualTo(THREAD_COUNT - INITIAL_QUANTITY);

        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getRemainingQuantity()).isZero();

        int issuedCount = couponItemRepository.countByCouponId(coupon.getId());
        assertThat(issuedCount).isEqualTo(INITIAL_QUANTITY);
    }

    private List<User> createTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(userRepository.save(User.of(1_000)));
        }
        return users;
    }
}
