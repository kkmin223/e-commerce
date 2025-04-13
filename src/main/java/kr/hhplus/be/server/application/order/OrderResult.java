package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class OrderResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrderAndPay {
        private Long orderId;
        private Integer totalAmount;
        private Integer paymentAmount;
        private LocalDateTime orderAt;
        private OrderStatus status;

        public static OrderAndPay createdBy(Order order) {
            return new OrderAndPay(order.getId(), order.getTotalAmount(), order.getPaymentAmount(), order.getOrderAt(), order.getStatus());
        }
    }
}
