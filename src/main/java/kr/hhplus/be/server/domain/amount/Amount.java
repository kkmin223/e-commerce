package kr.hhplus.be.server.domain.amount;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;

public record Amount(Integer amount) {
    private static final Integer MINIMUM_BALANCE_POLICY = 0;


    public Amount {
        if (amount < MINIMUM_BALANCE_POLICY) {
            throw new BusinessLogicException(ErrorCode.BELOW_MINIMUM_BALANCE_POLICY);
        }
    }

    public static Amount of(Integer amount) {
        return new Amount(amount);
    }

    public Amount plus(Integer amount) {
        return new Amount(this.amount + amount);
    }

    public Amount minus(Integer amount) {
        return new Amount(this.amount - amount);
    }
}
