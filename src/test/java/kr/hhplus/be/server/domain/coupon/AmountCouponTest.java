package kr.hhplus.be.server.domain.coupon;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void 쿠폰_타입을_조회하면_AMOUNT를_반환한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountAmount = 1_000;
        AmountCoupon coupon = AmountCoupon.of(title, initialQuantity, discountAmount);

        // when
        CouponType couponType = coupon.getCouponType();

        // then
        assertThat(couponType).isEqualTo(CouponType.AMOUNT);
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 1_000, 10_000, 100_000})
    void 쿠폰_라벨_정보를_조회하면_X원_할인을_반환한다(Integer discountAmount) {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        AmountCoupon coupon = AmountCoupon.of(title, initialQuantity, discountAmount);

        // when
        String discountLabel = coupon.getDiscountLabel();

        // then
        assertThat(discountLabel).isEqualTo(String.format("₩%,d 할인", discountAmount));
    }

}
