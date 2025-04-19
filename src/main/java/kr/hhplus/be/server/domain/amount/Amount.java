package kr.hhplus.be.server.domain.amount;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Amount {
    private static final Integer MINIMUM_BALANCE_POLICY = 0;

    @Column(nullable = false)
    private Integer amount;

    private Amount(Integer amount) {
        if (amount < MINIMUM_BALANCE_POLICY) {
            throw new BusinessLogicException(ErrorCode.BELOW_MINIMUM_BALANCE_POLICY);
        }
        this.amount = amount;
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
