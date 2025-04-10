package kr.hhplus.be.server.domain.couponItem;

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
}
