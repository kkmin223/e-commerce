package kr.hhplus.be.server.application.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Issue {
        private Long userId;
        private Long couponId;

        public static Issue of(Long userId, Long couponId) {
            return new Issue(userId, couponId);
        }
    }
}
