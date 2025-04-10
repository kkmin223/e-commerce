package kr.hhplus.be.server.domain.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Get {
        private Long couponId;

        public static Get of(Long couponId) {
            return new Get(couponId);
        }
    }
}
