package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
        description = "선착순 쿠폰 발급 요청 DTO"
    )
    public static class Issue {
        @Schema(
            description = "사용자 식별자",
            example = "1"
        )
        @Min(value = 1, message = "사용자 식별자가 유효하지 않습니다.")
        private long userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
        description = "선착순 쿠폰 발급 요청 DTO"
    )
    public static class Request {
        @Schema(
            description = "사용자 식별자",
            example = "1"
        )
        @Min(value = 1, message = "사용자 식별자가 유효하지 않습니다.")
        private long userId;
    }
}
