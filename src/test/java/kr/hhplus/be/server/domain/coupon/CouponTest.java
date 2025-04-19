package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @Test
    void 쿠폰_수량을_차감한다() {
        // given
        Integer initialQuantity = 10;
        TestCoupon testCoupon = new TestCoupon("테스트 쿠폰", initialQuantity);
        User user = User.of(1L, 1_000);

        // when
        testCoupon.decreaseRemainingQuantity();

        // then
        assertThat(testCoupon.getRemainingQuantity()).isEqualTo(initialQuantity - 1);
    }

    @Test
    void 쿠폰_수량이_0일때_차감하면_에러가_발생한다() {
        // given
        Integer initialQuantity = 0;
        TestCoupon testCoupon = new TestCoupon("쿠폰", initialQuantity);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> testCoupon.decreaseRemainingQuantity());

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(exception.getCode(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    void 남은_쿠폰_수량이_0보다_클_때_발급_가능_여부를_조회하면_TRUE를_반환한다(Integer initialQuantity) {
        // given
        TestCoupon testCoupon = new TestCoupon("쿠폰", initialQuantity);

        // when
        Boolean result = testCoupon.canIssue();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void 남은_쿠폰_수량이_0보다_작거나_같을때_발급_가능_여부를_조회하면_FALSE를_반환한다(Integer initialQuantity) {
        // given
        TestCoupon testCoupon = new TestCoupon("쿠폰", initialQuantity);

        // when
        Boolean result = testCoupon.canIssue();

        // then
        assertThat(result).isFalse();
    }

}
