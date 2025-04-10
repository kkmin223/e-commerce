package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientCouponQuantityException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @Test
    void 쿠폰_수량을_차감한다() {
        // given
        Integer initialQuantity = 10;
        AmountCoupon amountCoupon = AmountCoupon.of("쿠폰", initialQuantity, 1_000);
        User user = User.of(1L, 1_000);

        // when
        amountCoupon.decreaseRemainingQuantity();

        // then
        assertThat(amountCoupon.getRemainingQuantity()).isEqualTo(initialQuantity - 1);
    }

    @Test
    void 쿠폰_수량이_0일때_차감하면_에러가_발생한다() {
        // given
        Integer initialQuantity = 0;
        AmountCoupon amountCoupon = AmountCoupon.of("쿠폰", initialQuantity, 1_000);

        // when
        InsufficientCouponQuantityException exception = assertThrows(InsufficientCouponQuantityException.class, () -> amountCoupon.decreaseRemainingQuantity());

        // then
        assertThat(exception)
            .extracting(InsufficientCouponQuantityException::getCode, InsufficientCouponQuantityException::getMessage)
            .containsExactly(exception.getCode(), exception.getMessage());
    }

}
