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
        description = "사용자 식별자",
        example = "1"
    )
    private long userId;
    @Schema(
        description = "사용자 현재 잔액",
        example = "5000"
    )
    private int amount;
}
