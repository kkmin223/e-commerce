package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@DiscriminatorValue("TEST")
@Entity
public class TestCoupon extends Coupon {
    @Override
    public Integer apply(Integer amount) {
        return amount;
    }

    @Override
    public CouponType getCouponType() {
        return CouponType.AMOUNT;
    }

    @Override
    public String getDiscountLabel() {
        return "테스트쿠폰";
    }

    public TestCoupon(String title, Integer initialQuantity) {
        super(title, initialQuantity);
    }
}
