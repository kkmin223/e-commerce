package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "상품 응답 DTO")
    public static class Product {
        @Schema(
            description = "상품 고유번호",
            example = "1"
        )
        private Long id;
        @Schema(
            description = "상품명",
            example = "상품1"
        )
        private String name;
        @Schema(
            description = "상품 가격",
            example = "10000"
        )
        private Integer price;
        @Schema(
            description = "상품 수량",
            example = "10"
        )
        private Integer quantity;

        public static Product of(Long id, String name, Integer price, Integer quantity) {
            return new Product(id, name, price, quantity);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "인기 상품 응답 DTO")
    public static class ProductRanking {
        @Schema(
            description = "인기 상품 순위",
            example = "1"
        )
        private Integer ranking;
        @Schema(
            description = "상품 고유번호",
            example = "1"
        )
        private Long id;
        @Schema(
            description = "상품명",
            example = "상품1"
        )
        private String name;
        @Schema(
            description = "판매 수량",
            example = "10"
        )
        private Integer soldQuantity;

        public static ProductRanking of(Integer ranking, Long id, String name, Integer soldQuantity) {
            return new ProductRanking(ranking, id, name, soldQuantity);
        }
    }
}
