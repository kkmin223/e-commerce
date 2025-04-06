package kr.hhplus.be.server.domain.amount;

import kr.hhplus.be.server.interfaces.common.exceptions.BelowMinimumBalancePolicyException;

public record Amount(Integer balance) {
    private static final Integer MINIMUM_BALANCE_POLICY = 0;


    public Amount {
        if (balance < MINIMUM_BALANCE_POLICY) {
            throw new BelowMinimumBalancePolicyException();
        }
    }

    public static Amount of(Integer balance) {
        return new Amount(balance);
    }

    public Amount plus(Integer amount) {
        return new Amount(this.balance + amount);
    }

    public Amount minus(Integer amount) {
        return new Amount(this.balance - amount);
    }
}
