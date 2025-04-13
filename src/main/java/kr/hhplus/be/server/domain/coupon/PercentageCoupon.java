package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PERCENTAGE")
public class PercentageCoupon extends Coupon {
    private Integer discountRate;

    private PercentageCoupon(String title, Integer initialQuantity, Integer discountRate) {
        super(title, initialQuantity);
        this.discountRate = discountRate;
    }

    public static PercentageCoupon of(String title, Integer initialQuantity, Integer discountRate) {
        return new PercentageCoupon(title, initialQuantity, discountRate);
    }

    @Override
    public Integer apply(Integer amount) {
        return (int) (amount * (Double.valueOf(100 - discountRate) / 100));
    }

    @Override
    public CouponType getCouponType() {
        return CouponType.PERCENTAGE;
    }

    @Override
    public String getDiscountLabel() {
        return String.format("%d%% 할인", discountRate);
    }
}
