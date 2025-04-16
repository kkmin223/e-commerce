package kr.hhplus.be.server.application.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Product {
        private Long id;
        private String name;
        private Integer price;
        private Integer quantity;

        public static Product of(Long id, String name, Integer price, Integer quantity) {
            return new Product(id, name, price, quantity);
        }
    }
}
