package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserCoupon {
        private Long id;
        private String couponName;
        private Boolean isUsed;
        private String discountLabel;
        private CouponType couponType;

        public static UserCoupon of(Long id, String couponName, Boolean isUsed, String discountLabel, CouponType couponType) {
            return new UserCoupon(id, couponName, isUsed, discountLabel, couponType);
        }
    }
}
