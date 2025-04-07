package kr.hhplus.be.server.domain.amount;

import kr.hhplus.be.server.interfaces.common.exceptions.BelowMinimumBalancePolicyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountTest {

    @Test
    void 잔액을_생성할_수_있다() {
        // given
        Integer inputAmount = 100;

        // when
        Amount amount = Amount.of(inputAmount);

        // then
        assertThat(amount.amount())
            .isEqualTo(inputAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100})
    void 잔액을_생성할_때_0원_아래_금액은_생성할_수_없습니다(Integer inputAmount) {
        // when
        BelowMinimumBalancePolicyException exception = assertThrows(BelowMinimumBalancePolicyException.class, () -> Amount.of(inputAmount));

        // then
        assertThat(exception.getMessage())
            .isEqualTo("금액이 최소 정책 금액보다 작을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(ints = {101, 200})
    void 잔액을_차감한_이후_잔액은_음수일_수_없습니다(Integer deductAmount) {
        // given
        Amount amount = Amount.of(100);

        // when
        BelowMinimumBalancePolicyException exception = assertThrows(BelowMinimumBalancePolicyException.class, () -> amount.minus(deductAmount));

        // then
        assertThat(exception.getMessage())
            .isEqualTo("금액이 최소 정책 금액보다 작을 수 없습니다.");
    }
}

