package kr.hhplus.be.server.domain.coupon;

import lombok.*;

public class CouponCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Get {
        private Long couponId;

        public static Get of(Long couponId) {
            return new Get(couponId);
        }
    }
}
