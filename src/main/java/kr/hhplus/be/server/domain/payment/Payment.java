package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientBalanceException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Payment {
    private Long id;
    private Integer paymentAmount;
    private User user;
    private Order order;
    private CouponItem couponItem;

    private Payment(Order order, User user) {
        this.order = order;
        this.user = user;
        this.paymentAmount = order.getTotalAmount();
    }

    public static Payment create(Order order, User user) {
        return new Payment(order, user);
    }

    public void applyCoupon(CouponItem couponItem) {
        this.paymentAmount = couponItem.applyCoupon(this.paymentAmount);
        this.couponItem = couponItem;
    }

    public void processPayment() {
        if (!this.user.canPay(this.paymentAmount)) {
            throw new InsufficientBalanceException();
        }
        user.deductAmount(this.paymentAmount);
        order.completeOrder(this.paymentAmount);
    }
}
