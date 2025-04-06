package kr.hhplus.be.server.domain.user;


import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidChargeAmountException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidDeductAmountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void 잔액을_충전하면_충전_이후_금액을_반환한다() {
        // given
        Long userId = 1L;
        Integer amount = 100;
        Integer chargeAmount = 100;

        User user = User.createdBy(userId, amount);
        // when
        user.chargeAmount(chargeAmount);

        // then
        assertThat(user.getAmount()).isEqualTo(amount + chargeAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 잔액_충전시_0원_이하_금액을_충전할_수_없습니다(Integer chargeAmount) {
        // given
        Long userId = 1L;
        Integer amount = 0;
        User user = User.createdBy(userId, amount);

        // when
        InvalidChargeAmountException exception = Assertions.assertThrows(InvalidChargeAmountException.class, () -> user.chargeAmount(chargeAmount));

        // then
        assertThat(exception)
            .extracting(InvalidChargeAmountException::getCode, InvalidChargeAmountException::getMessage)
            .containsExactly(ErrorCode.INVALID_CHARGE_AMOUNT.getCode(), ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());

    }

    @Test
    void 잔액을_차감하면_차감_이후_금액을_반환한다() {
        // given
        Long userId = 1L;
        Integer amount = 500;
        Integer deductAmount = 100;

        User user = User.createdBy(userId, amount);
        // when
        user.deductAmount(deductAmount);

        // then
        assertThat(user.getAmount()).isEqualTo(amount - deductAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 잔액_차감시_0원_이하_금액을_차감할_수_없습니다(Integer deductAmount) {
        // given
        Long userId = 1L;
        Integer amount = 0;
        User user = User.createdBy(userId, amount);

        // when
        InvalidDeductAmountException exception = Assertions.assertThrows(InvalidDeductAmountException.class, () -> user.deductAmount(deductAmount));

        // then
        assertThat(exception)
            .extracting(InvalidDeductAmountException::getCode, InvalidDeductAmountException::getMessage)
            .containsExactly(ErrorCode.INVALID_DEDUCT_AMOUNT.getCode(), ErrorCode.INVALID_DEDUCT_AMOUNT.getMessage());

    }
}
