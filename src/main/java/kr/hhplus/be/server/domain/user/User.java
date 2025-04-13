package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.amount.Amount;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Amount amount;

    private User(Long userId, Amount amount) {
        this.id = userId;
        this.amount = amount;
    }

    public static User of(Long userId, Integer amount) {
        return new User(userId, Amount.of(amount));
    }

    public Integer getAmount() {
        return amount.getAmount();
    }

    public void chargeAmount(Integer amount) {
        if (amount <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }
        this.amount = this.amount.plus(amount);
    }

    public void deductAmount(Integer amount) {
        if (amount <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_DEDUCT_AMOUNT);
        }
        this.amount = this.amount.minus(amount);
    }

    public Boolean canPay(Integer amount) {
        return this.amount.getAmount() >= amount;
    }
}
