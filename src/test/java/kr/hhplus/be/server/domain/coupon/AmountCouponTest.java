package kr.hhplus.be.server.domain.coupon;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AmountCouponTest {

    @Test
    void 정액_할인_쿠폰을_생성한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountAmount = 1_000;

        // when
        AmountCoupon coupon = AmountCoupon.of(title, initialQuantity, discountAmount);

        // then
        assertThat(coupon)
            .extracting(AmountCoupon::getTitle, AmountCoupon::getInitialQuantity, AmountCoupon::getRemainingQuantity, AmountCoupon::getDiscountAmount)
            .containsExactly(title, initialQuantity, initialQuantity, discountAmount);
    }

    @Test
    void 정액_할인_쿠폰을_적용한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountAmount = 1_000;
        Integer amount = 2_000;


        AmountCoupon coupon = AmountCoupon.of(title, initialQuantity, discountAmount);
        // when
        Integer appliedAmount = coupon.apply(amount);

        // then
        assertThat(appliedAmount)
            .isEqualTo(amount - discountAmount);
    }

    @Test
    void 쿠폰_적용_후_금액이_음수이면_0원을_반환한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountAmount = 1_000;
        Integer amount = 500;


        AmountCoupon coupon = AmountCoupon.of(title, initialQuantity, discountAmount);
        // when
        Integer appliedAmount = coupon.apply(amount);

        // then
        assertThat(appliedAmount)
            .isEqualTo(0);
    }
}
