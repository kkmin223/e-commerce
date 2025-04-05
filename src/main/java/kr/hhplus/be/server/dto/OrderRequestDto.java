package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "주문 요청 DTO"
)
public class OrderRequestDto {
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
    private List<OrderProductRequestDto> orderProducts;
}
