package kr.hhplus.be.server.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private Long id;
        private String name;
        private Integer quantity;
        private Integer price;

        public static Product createdBy(kr.hhplus.be.server.domain.product.Product product) {
            return new Product(product.getId(), product.getName(), product.getQuantity(), product.getPrice());
        }
    }
}
