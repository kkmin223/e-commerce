package kr.hhplus.be.server.interfaces.user;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "잔액 응답 DTO")
    public static class UserAmount {
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "사용자 보유 쿠폰 조회 응답 DTO")
    public static class UserCoupon {
        @Schema(
            description = "사용자 식별자",
            example = "1"
        )
        private long userId;
        @Schema(
            description = "보유 쿠폰 리스트"
        )
        private List<CouponResponse.Coupon> couponse;
    }
}
