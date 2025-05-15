package kr.hhplus.be.server.infrastructure.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ProductRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void incrementProductDailyScore(LocalDate orderAt, Long productId, Integer orderQuantity, Duration ttlDuration) {
        String key = "PRODUCT:DAILY:" + orderAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value = productId.toString();

        redisTemplate.opsForZSet().incrementScore(key, value, orderQuantity.intValue());

        if (redisTemplate.getExpire(key) < 0) {
            redisTemplate.expire(key, ttlDuration);
        }
    }

    public Set<ZSetOperations.TypedTuple<String>> getProductSales(LocalDate searchDate) {
        String key = "PRODUCT:DAILY:" + searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    public Set<ZSetOperations.TypedTuple<String>> getProductRanking(LocalDate startDate, LocalDate endDate, int rankCount) {
        String tempKey = "PRODUCT:UNION:" + UUID.randomUUID();

        List<String> keys = startDate.datesUntil(endDate.plusDays(1)) // endDate 포함
            .map(date -> "PRODUCT:DAILY:" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .toList();

        try {
            redisTemplate.opsForZSet().unionAndStore(keys.get(0),
                keys.subList(1, keys.size()),
                tempKey,
                Aggregate.SUM
            );

            return redisTemplate.opsForZSet().reverseRangeWithScores(tempKey, 0, rankCount - 1);

        } finally {
            redisTemplate.delete(tempKey);
        }

    }
}
