package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 쿠폰_식별자를_입력받아_발급할_수_있는_쿠폰이면_반환한다() {
        // given
        TestCoupon testCoupon = new TestCoupon("쿠폰", 10);
        Coupon savedCoupon = couponRepository.save(testCoupon);
        CouponCommand.Get command = CouponCommand.Get.of(savedCoupon.getId());

        couponRepository.save(testCoupon);

        // when
        Coupon result = couponService.getIssuableCoupon(command);

        // then
        assertThat(result)
            .extracting(Coupon::getId, Coupon::getTitle, Coupon::getInitialQuantity, Coupon::getRemainingQuantity)
            .containsExactly(testCoupon.getId(), testCoupon.getTitle(), testCoupon.getInitialQuantity(), testCoupon.getRemainingQuantity());
    }

    @Test
    void 쿠폰_발급을_요청한다() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime now = LocalDateTime.now();
        CouponCommand.Request command = CouponCommand.Request.of(couponId, userId, now);

        String key = "COUPON:REQUEST:" + couponId;

        // when
        couponService.requestCoupon(command);

        // then
        Set<String> expected = redisTemplate.opsForZSet().range(key, 0, -1);
        assertThat(expected)
            .hasSize(1)
            .containsExactly(userId.toString());

    }

    @Test
    void 발급_요청된_쿠폰의_사용자_식별자를_조회한다() {
        // given
        Long couponId = 1L;
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long userId3 = 3L;
        LocalDateTime issuedAt1 = LocalDateTime.now();
        LocalDateTime issuedAt2 = issuedAt1.plusMinutes(1);
        LocalDateTime issuedAt3 = issuedAt2.plusMinutes(1);

        String key = "COUPON:REQUEST:" + couponId;
        redisTemplate.opsForZSet().add(key, userId1.toString(), issuedAt1.toEpochSecond(ZoneOffset.UTC));
        redisTemplate.opsForZSet().add(key, userId2.toString(), issuedAt2.toEpochSecond(ZoneOffset.UTC));
        redisTemplate.opsForZSet().add(key, userId3.toString(), issuedAt3.toEpochSecond(ZoneOffset.UTC));

        // when
        Set<String> userIds = couponService.getIssueRequest(CouponCommand.GetIssueRequest.of(couponId, 2));

        // then
        assertThat(userIds)
            .hasSize(2)
            .containsExactly(
                userId1.toString(),
                userId2.toString()
            );


    }
}
