package kr.hhplus.be.server.infrastructure.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@SpringBootTest
class ProductRedisRepositoryTest {

    @Autowired
    private ProductRedisRepository productRedisRepository;

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
    void 상품_일간_판매량을_sorted_set에_저장한다() {
        // given
        LocalDate orderAt = LocalDate.of(2024, 5, 15);
        Long productId = 1L;
        Integer orderQuantity = 3;
        Duration ttlDuration = Duration.ofDays(1);

        String key = "PRODUCT:DAILY:" + orderAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value = productId.toString();

        // when
        productRedisRepository.incrementProductDailyScore(orderAt, productId, orderQuantity, ttlDuration);

        // then
        Set<ZSetOperations.TypedTuple<String>> expectedTuple = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        Assertions.assertThat(expectedTuple)
            .hasSize(1)
            .extracting(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore)
            .containsExactly(Tuple.tuple(value, orderQuantity.doubleValue()));
    }

    @Test
    void 상품_일간_판매량을_조회한다() {
        // given
        LocalDate searchDate = LocalDate.of(2024, 5, 15);
        Long productId = 1L;
        Integer orderQuantity = 3;

        String key = "PRODUCT:DAILY:" + searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value = productId.toString();

        redisTemplate.opsForZSet().incrementScore(key, value, orderQuantity.intValue());
        // when
        Set<ZSetOperations.TypedTuple<String>> productSales = productRedisRepository.getProductSales(searchDate);

        // then
        Assertions.assertThat(productSales)
            .hasSize(1)
            .extracting(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore)
            .containsExactly(Tuple.tuple(value, orderQuantity.doubleValue()));
    }

    @Test
    void 상품_일간_판매량을_합친_결과를_조회한다() {
        // given
        LocalDate date1 = LocalDate.of(2025, 5, 13);
        LocalDate date2 = LocalDate.of(2025, 5, 14);
        LocalDate date3 = LocalDate.of(2025, 5, 15);
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long productId3 = 3L;
        Integer orderQuantity1 = 3;
        Integer orderQuantity2 = 3;
        Integer orderQuantity3 = 3;

        String key1 = "PRODUCT:DAILY:" + date1.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key2 = "PRODUCT:DAILY:" + date2.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key3 = "PRODUCT:DAILY:" + date3.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value1 = productId1.toString();
        String value2 = productId2.toString();
        String value3 = productId3.toString();

        redisTemplate.opsForZSet().incrementScore(key1, value1, orderQuantity1.intValue());
        redisTemplate.opsForZSet().incrementScore(key1, value2, orderQuantity2.intValue());
        redisTemplate.opsForZSet().incrementScore(key2, value2, orderQuantity2.intValue());
        redisTemplate.opsForZSet().incrementScore(key3, value3, orderQuantity3.intValue());
        // when
        Set<ZSetOperations.TypedTuple<String>> productSales = productRedisRepository.getProductRanking(date1, date3, 1);

        // then
        Assertions.assertThat(productSales)
            .hasSize(1)
            .extracting(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore)
            .containsExactly(
                Tuple.tuple(value2, 6.0)
            );
    }
}
