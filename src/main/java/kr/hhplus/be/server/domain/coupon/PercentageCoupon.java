package kr.hhplus.be.server.domain.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
}
