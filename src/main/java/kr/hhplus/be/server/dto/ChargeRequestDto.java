package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Getter
@NoArgsConstructor
@Setter
@Schema(description = "잔액 충전 요청 DTO")
public class ChargeRequestDto {
    @Schema(
        description = "충전 금액",
        example = "5000"
    )
    private int amount;

    @Builder(toBuilder = true)
    public ChargeRequestDto(int amount) {
        this.amount = amount;
    }
}
