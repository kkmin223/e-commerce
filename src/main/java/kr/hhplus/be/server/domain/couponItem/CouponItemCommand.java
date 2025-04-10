package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponItemCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Get {
        private Long couponItemId;

        public static Get of(Long couponItemId) {
            return new Get(couponItemId);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Issue {
        private Coupon coupon;
        private User user;

        public static Issue of(Coupon coupon, User user) {
            return new Issue(coupon, user);
        }
    }
}
