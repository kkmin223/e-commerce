package kr.hhplus.be.server.interfaces.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "잔액 충전 요청 DTO")
    public static class Charge {
        @Schema(
            description = "충전 금액",
            example = "5000"
        )
        @Min(value = 1, message = "충전 금액이 유효하지 않습니다.")
        private int amount;
    }
}
