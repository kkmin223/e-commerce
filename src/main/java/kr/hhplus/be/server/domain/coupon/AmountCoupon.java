package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("AMOUNT")
public class AmountCoupon extends Coupon {
    private Integer discountAmount;

    private AmountCoupon(String title, Integer initialQuantity, Integer discountAmount) {
        super(title, initialQuantity);
        this.discountAmount = discountAmount;
    }

    public static AmountCoupon of(String title, Integer initialQuantity, Integer discountAmount) {
        return new AmountCoupon(title, initialQuantity, discountAmount);
    }

    @Override
    public Integer apply(Integer amount) {
        return Math.max(0, amount - discountAmount);
    }

    @Override
    public CouponType getCouponType() {
        return CouponType.AMOUNT;
    }

    @Override
    public String getDiscountLabel() {
        return String.format("₩%,d 할인", discountAmount);
    }
}
