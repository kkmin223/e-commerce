package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "주문 응답 DTO"
)
public class OrderResponseDto {
    @Schema(
        description = "주문 고유번호",
        example = "1"
    )
    private long orderId;
    @Schema(
        description = "주문 총 금액",
        example = "10000"
    )
    private int totalAmount;
    @Schema(
        description = "최종 결제 금액",
        example = "5000"
    )
    private int paymentAmount;
    @Schema(
        description = "주문 시간",
        example = "2025-04-04 10:00"
    )
    private String orderAt;
    @Schema(
        description = "주문 상태",
        example = "COMPLETED"
    )
    private OrderStatus status;
}
