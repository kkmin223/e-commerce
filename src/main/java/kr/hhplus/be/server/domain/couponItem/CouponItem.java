package kr.hhplus.be.server.domain.couponItem;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CouponItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
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
            throw new BusinessLogicException(ErrorCode.COUPON_ALREADY_USED);
        }

        Integer appliedAmount = coupon.apply(amount);
        markAsUsed();
        return appliedAmount;
    }

    private void markAsUsed() {
        this.isUsed = true;
    }

    public static CouponItem issue(User user, Coupon coupon) {
        if (!coupon.canIssue()) {
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }

        coupon.decreaseRemainingQuantity();
        return new CouponItem(user, coupon, false);
    }

}
