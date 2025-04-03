package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "잔액 응답 DTO")
public class AmountResponseDto {
    @Schema(
        description = "사용자 고유 ID",
        example = "1"
    )
    private Long userId;
    @Schema(
        description = "사용자 현재 잔액",
        example = "5000"
    )
    private Integer amount;
}
