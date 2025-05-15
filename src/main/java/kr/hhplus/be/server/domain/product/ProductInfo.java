package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;

public class ProductInfo {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSalesInfo {
        private Long productId;
        private Integer quantity;

        public static ProductSalesInfo of(ZSetOperations.TypedTuple<String> productScore) {
            return new ProductSalesInfo(Long.valueOf(productScore.getValue()), productScore.getScore().intValue());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRanking {
        private int ranking;
        private Long productId;
        private String productName;
        private int soldQuantity;

        public static ProductRanking of(int ranking, Long productId, String productName, int soldQuantity) {
            return new ProductRanking(ranking, productId, productName, soldQuantity);
        }
    }
}
