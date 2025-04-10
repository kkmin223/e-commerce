package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.amount.Amount;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidChargeAmountException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidDeductAmountException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private Amount amount;
    private List<CouponItem> couponItems;

    private User(Long userId, Amount amount) {
        this.id = userId;
        this.amount = amount;
        this.couponItems = new ArrayList<>();
    }

    public static User of(Long userId, Integer amount) {
        return new User(userId, Amount.of(amount));
    }

    public Integer getAmount() {
        return amount.amount();
    }

    public void chargeAmount(Integer amount) {
        if (amount <= 0) {
            throw new InvalidChargeAmountException();
        }
        this.amount = this.amount.plus(amount);
    }

    public void deductAmount(Integer amount) {
        if (amount <= 0) {
            throw new InvalidDeductAmountException();
        }
        this.amount = this.amount.minus(amount);
    }

    public Boolean canPay(Integer amount) {
        return this.amount.amount() >= amount;
    }
}
