package kr.hhplus.be.server.domain.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponEvent {
    private Long couponId;
    private Long userId;

    public static CouponEvent of(Long couponId, Long userId) {
        return new CouponEvent(couponId, userId);
    }
}
