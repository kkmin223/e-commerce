package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
        description = "쿠폰 응답 DTO"
    )
    public static class Coupon {
        @Schema(
            description = "쿠폰 아이템 고유번호",
            example = "1"
        )
        private long id;
        @Schema(
            description = "쿠폰명",
            example = "쿠폰1"
        )
        private String couponName;
        @Schema(
            description = "사용 여부",
            example = "true"
        )
        private Boolean isUsed;
        @Schema(
            description = "쿠폰 할인 값",
            example = "1000"
        )
        private int discountValue;
        @Schema(
            description = "쿠폰 타입",
            example = "AMOUNT"
        )
        private CouponType couponType;
    }
}
