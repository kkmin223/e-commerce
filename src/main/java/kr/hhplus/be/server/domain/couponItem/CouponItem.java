package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.exceptions.CouponAlreadyUsedException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponItem {
    private Long id;
    private User user;
    private Coupon coupon;
    private Boolean isUsed;

    private CouponItem(User user, Coupon coupon, Boolean isUsed) {
        this.user = user;
        this.coupon = coupon;
        this.isUsed = isUsed;
    }

    public static CouponItem of(User user, Coupon coupon, Boolean isUsed) {
        return new CouponItem(user, coupon, isUsed);
    }

    public Integer applyCoupon(Integer amount) {
        if (isUsed) {
            throw new CouponAlreadyUsedException();
        }

        Integer appliedAmount = coupon.apply(amount);
        markAsUsed();
        return appliedAmount;
    }

    private void markAsUsed() {
        this.isUsed = true;
    }

    public static CouponItem issue(User user, Coupon coupon) {
        coupon.decreaseRemainingQuantity();
        return new CouponItem(user, coupon, false);
    }

}
