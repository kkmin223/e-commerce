package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "주문 상품 요청 DTO"
)
public class OrderProductRequestDto {
    @Schema(
        description = "상품 식별자",
        example = "1"
    )
    private Long productId;
    @Schema(
        description = "주문 수량",
        example = "10"
    )
    @Min(value = 1, message = "주문 수량이 유효하지 않습니다.")
    private Integer quantity;
}
