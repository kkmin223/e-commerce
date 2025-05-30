package kr.hhplus.be.server.application.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrderCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrderAndPay {
        private Long userId;
        private Long couponItemId;
        private List<OrderProduct> orderProducts;
        private LocalDateTime orderAt;

        public static OrderAndPay of(Long userId, Long couponItemId, List<OrderProduct> orderProducts, LocalDateTime orderAt) {
            return new OrderAndPay(userId, couponItemId, orderProducts, orderAt);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrderProduct {
        private Long productId;
        private Integer quantity;

        public static OrderProduct of(Long productId, Integer quantity) {
            return new OrderProduct(productId, quantity);
        }
    }
}

