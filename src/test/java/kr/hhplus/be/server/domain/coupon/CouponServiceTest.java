package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_식별자를_입력받아_발급할_수_있는_쿠폰이면_반환한다() {
        // given
        Long couponId = 1L;
        CouponCommand.Get command = CouponCommand.Get.of(couponId);
        TestCoupon testCoupon = new TestCoupon("쿠폰", 10);

        Mockito.when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        // when
        Coupon result = couponService.getIssuableCoupon(command);

        // then
        assertThat(result)
            .extracting(Coupon::getId, Coupon::getTitle, Coupon::getInitialQuantity, Coupon::getRemainingQuantity)
            .containsExactly(testCoupon.getId(), testCoupon.getTitle(), testCoupon.getInitialQuantity(), testCoupon.getRemainingQuantity());
    }

    @Test
    void 쿠폰_식별자가_NULL이면_쿠폰_반환을_실패한다() {
        // given
        Long couponId = null;
        CouponCommand.Get command = CouponCommand.Get.of(couponId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponService.getIssuableCoupon(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_COUPON_ID.getCode(), ErrorCode.INVALID_COUPON_ID.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void 쿠폰_식별자가_0이하이면_쿠폰_반환을_실패한다(Long couponId) {
        // given
        CouponCommand.Get command = CouponCommand.Get.of(couponId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponService.getIssuableCoupon(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_COUPON_ID.getCode(), ErrorCode.INVALID_COUPON_ID.getMessage());
    }

    @Test
    void 쿠폰_잔여_수량이_0인경우_쿠폰_반환을_실패한다() {
        // given
        Long couponId = 1L;
        CouponCommand.Get command = CouponCommand.Get.of(couponId);
        TestCoupon testCoupon = new TestCoupon("쿠폰", 0);

        Mockito.when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponService.getIssuableCoupon(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getCode(), ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }

}
