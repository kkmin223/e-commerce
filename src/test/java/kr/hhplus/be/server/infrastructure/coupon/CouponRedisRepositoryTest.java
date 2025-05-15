package kr.hhplus.be.server.infrastructure.coupon;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponRedisRepositoryTest {

    @Autowired
    private CouponRedisRepository couponRedisRepository;

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
    void 쿠폰_대기열에_추가한다() {
        // given
        Long couponId = 1L;
        Long userId = 1L;
        LocalDateTime issuedAt = LocalDateTime.now();

        // when
        couponRedisRepository.requestCoupon(couponId, userId, issuedAt);

        // then
        Set<ZSetOperations.TypedTuple<String>> expected = redisTemplate.opsForZSet().reverseRangeWithScores("COUPON:REQUEST:" + couponId, 0, -1);
        assertThat(expected).hasSize(1)
            .extracting(ZSetOperations.TypedTuple::getValue, tuple -> tuple.getScore().longValue())
            .containsExactlyInAnyOrder(
                Tuple.tuple(userId.toString(), issuedAt.toEpochSecond(ZoneOffset.UTC))
            );
    }

    @Test
    void 쿠폰_대기열을_시간_내림차순으로_조회한다() {
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
        Set<String> issueRequest = couponRedisRepository.getIssueRequest(couponId, 3);

        // then
        assertThat(issueRequest)
            .hasSize(3)
            .containsExactly(userId1.toString(), userId2.toString(), userId3.toString());
    }
}
