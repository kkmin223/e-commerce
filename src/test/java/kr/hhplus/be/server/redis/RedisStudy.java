package kr.hhplus.be.server.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisStudy {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void sorted_set_예시() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String redisKey1 = "product_ranking:daily:20250513";
        String redisKey2 = "product_ranking:daily:20250512";

        redisTemplate.delete(redisKey1);
        redisTemplate.delete(redisKey2);

        // product ID 별 점수 증가
        String productId1 = "1";
        String productId2 = "2";
        String productId3 = "3";
        String productId4 = "4";
        zSetOperations.incrementScore(redisKey1, productId1, 5.0);
        zSetOperations.incrementScore(redisKey1, productId2, 2.0);
        zSetOperations.incrementScore(redisKey1, productId3, 10.0);
        zSetOperations.incrementScore(redisKey1, productId4, 5.0);

        Double productIdScore1_1 = zSetOperations.score(redisKey1, productId1);
        assertThat(productIdScore1_1).isEqualTo(5.0);

        // productId1 점수 증가
        zSetOperations.incrementScore(redisKey1, productId1, 1.0);
        Double productIdScore1_2 = zSetOperations.score(redisKey1, productId1);
        assertThat(productIdScore1_2).isEqualTo(6.0);

        // 랭킹 조회
        Set<String> strings = zSetOperations.reverseRange(redisKey1, 0, 1);
        assertThat(strings)
            .hasSize(2)
            .containsExactly(productId3, productId1);

    }

}
