package kr.hhplus.be.server.domain.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
