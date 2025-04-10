package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Issue {
        private long id;
        private String couponName;
        private Boolean isUsed;
        private String discountLabel;
        private CouponType couponType;

        public static Issue of(long id, String couponName, Boolean isUsed, String discountLabel, CouponType couponType) {
            return new Issue(id, couponName, isUsed, discountLabel, couponType);
        }
    }

}
