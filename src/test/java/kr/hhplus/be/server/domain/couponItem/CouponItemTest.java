package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CouponItemTest {

    @Test
    void 쿠폰_아이템을_생성합니다() {
        // given
        Coupon amountCoupon = AmountCoupon.of("정액 쿠폰", 10, 1000);
        User user = User.of(1L, 10_000);

        // when
        CouponItem couponItem = CouponItem.of(user, amountCoupon, Boolean.FALSE);

        // then
        assertThat(couponItem)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(user, amountCoupon, Boolean.FALSE);
    }

    @Test
    void 쿠폰_아이템을_사용합니다() {
        // given
        Integer discountAmount = 1_000;
        Coupon amountCoupon = AmountCoupon.of("정액 쿠폰", 10, discountAmount);
        User user = User.of(1L, 10_000);
        Integer amount = 10_000;

        CouponItem couponItem = CouponItem.of(user, amountCoupon, Boolean.FALSE);
        // when
        Integer appliedAmount = couponItem.applyCoupon(amount);

        // then
        assertThat(appliedAmount).isEqualTo(amount - discountAmount);
        assertThat(couponItem.getIsUsed()).isEqualTo(Boolean.TRUE);
    }

    @Test
    void 이미_사용한_쿠폰을_사용하면_사용에_실패합니다() {
        // given
        Integer discountAmount = 1_000;
        Coupon amountCoupon = AmountCoupon.of("정액 쿠폰", 10, discountAmount);
        User user = User.of(1L, 10_000);
        Integer amount = 10_000;

        CouponItem couponItem = CouponItem.of(user, amountCoupon, Boolean.TRUE);
        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> couponItem.applyCoupon(amount));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.COUPON_ALREADY_USED.getCode(), ErrorCode.COUPON_ALREADY_USED.getMessage());
    }

    @Test
    void 쿠폰과_사용자를_전달받아_쿠폰_아이템을_발급한다() {
        // given
        Integer initialQuantity = 10;
        TestCoupon coupon = new TestCoupon("쿠폰", initialQuantity);
        User user = User.of(1L, 10_000);

        // when
        CouponItem issuedCouponItem = CouponItem.issue(user, coupon);

        // then
        assertThat(issuedCouponItem)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(user, coupon, Boolean.FALSE);

        assertThat(issuedCouponItem.getCoupon().getRemainingQuantity())
            .isEqualTo(initialQuantity - 1);

    }

    @Test
    void 남은_수량이_0인_쿠폰과_사용자를_전달받아_쿠폰_아이템을_발급하면_발급에_실패한다() {
        // given
        Integer initialQuantity = 0;
        TestCoupon coupon = new TestCoupon("쿠폰", initialQuantity);
        User user = User.of(1L, 10_000);

        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> CouponItem.issue(user, coupon));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getCode(), ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());

    }

}
