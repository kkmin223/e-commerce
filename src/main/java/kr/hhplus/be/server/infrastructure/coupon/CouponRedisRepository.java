package kr.hhplus.be.server.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CouponRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void requestCoupon(Long couponId, Long userId, LocalDateTime issuedAt) {
        String key = "COUPON:REQUEST:" + couponId;
        String value = userId.toString();
        redisTemplate.opsForZSet().add(key, value, issuedAt.toEpochSecond(ZoneOffset.UTC));
    }

    public Set<String> getIssueRequest(Long couponId, Integer count) {
        String key = "COUPON:REQUEST:" + couponId;
        return redisTemplate.opsForZSet().range(key, 0, count - 1);
    }

    public void deleteIssueRequest(Long couponId, Long userId) {
        String key = "COUPON:REQUEST:" + couponId;
        String value = userId.toString();
        redisTemplate.opsForZSet().remove(key, value);
    }

    public Long addIssuedUser(Long couponId, Long userId) {
        String key = "COUPON:ISSUED:" + couponId;
        String value = userId.toString();
        return redisTemplate.opsForSet().add(key, value);
    }
}
