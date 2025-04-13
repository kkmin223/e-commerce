package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageCouponTest {

    @Test
    void 비율_할인_쿠폰을_생성한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountRate = 10;

        // when
        PercentageCoupon coupon = PercentageCoupon.of(title, initialQuantity, discountRate);

        // then
        assertThat(coupon)
            .extracting(PercentageCoupon::getTitle, PercentageCoupon::getInitialQuantity, PercentageCoupon::getRemainingQuantity, PercentageCoupon::getDiscountRate)
            .containsExactly(title, initialQuantity, initialQuantity, discountRate);
    }

    @Test
    void 비율_할인_쿠폰을_적용한다() {
        // given
        String title = "쿠폰";
        Integer initialQuantity = 100;
        Integer discountRate = 10;
        Integer amount = 1000;

        PercentageCoupon coupon = PercentageCoupon.of(title, initialQuantity, discountRate);

        // when
        Integer appliedAmount = coupon.apply(amount);

        // then
        assertThat(appliedAmount)
            .isEqualTo(900);
    }
}
