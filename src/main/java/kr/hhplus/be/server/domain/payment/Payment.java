package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer paymentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Order order;

    @OneToOne(fetch = FetchType.EAGER)
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
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        user.deductAmount(this.paymentAmount);
        order.completeOrder(this.paymentAmount);
    }
}
