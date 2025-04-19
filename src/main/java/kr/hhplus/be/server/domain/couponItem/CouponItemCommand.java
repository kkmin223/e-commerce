package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import lombok.*;

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
    @EqualsAndHashCode
    public static class Issue {
        private Coupon coupon;
        private User user;

        public static Issue of(Coupon coupon, User user) {
            return new Issue(coupon, user);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class FindByUser {
        private User user;

        public static FindByUser of(User user) {
            return new FindByUser(user);
        }
    }
}
