package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.ProductResult;
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
        private long id;
        @Schema(
            description = "상품명",
            example = "상품1"
        )
        private String name;
        @Schema(
            description = "상품 가격",
            example = "10000"
        )
        private int price;
        @Schema(
            description = "상품 수량",
            example = "10"
        )
        private int quantity;

        public static Product created(ProductResult.Product product) {
            return new Product(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
        }
    }
}
