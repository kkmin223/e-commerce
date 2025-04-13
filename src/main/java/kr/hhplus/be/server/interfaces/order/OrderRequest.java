package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.application.order.OrderCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrderRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
        description = "주문 요청 DTO"
    )
    public static class Order {
        @Schema(
            description = "유저 식별자",
            example = "1"
        )
        @Min(value = 1, message = "유저 식별자가 유효하지 않습니다.")
        private long userId;
        @Schema(
            description = "쿠폰 아이템 식별자",
            example = "1"
        )
        private long couponItemId;
        @Schema(
            description = "주문 상품 목록"
        )
        private List<OrderProduct> orderProducts;

        public OrderCriteria.OrderAndPay toCriteria() {
            return OrderCriteria.OrderAndPay.of(userId
                , couponItemId
                , orderProducts.stream().map(orderProduct -> OrderCriteria.OrderProduct.of(orderProduct.productId, orderProduct.quantity)).toList());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
        description = "주문 상품 요청 DTO"
    )
    public static class OrderProduct {
        @Schema(
            description = "상품 식별자",
            example = "1"
        )
        private long productId;
        @Schema(
            description = "주문 수량",
            example = "10"
        )
        @Min(value = 1, message = "주문 수량이 유효하지 않습니다.")
        private int quantity;
    }
}
