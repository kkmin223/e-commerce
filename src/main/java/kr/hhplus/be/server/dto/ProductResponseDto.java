package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답 DTO")
public class ProductResponseDto {
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
}
