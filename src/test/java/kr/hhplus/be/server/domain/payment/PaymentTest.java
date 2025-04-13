package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    @Test
    void 결제를_생성한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        Order order = Order.create(user, productQuantities);
        // when
        Payment payment = Payment.create(order, user);

        // then
        assertThat(payment)
            .extracting(Payment::getUser, Payment::getOrder, Payment::getPaymentAmount)
            .containsExactly(user, order, order.getTotalAmount());
    }

    @Test
    void 결제에_쿠폰을_적용한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        Order order = Order.create(user, productQuantities);
        AmountCoupon coupon = AmountCoupon.of("정액 쿠폰", 10, 1000);
        CouponItem couponItem = CouponItem.of(user, coupon, Boolean.FALSE);

        Payment payment = Payment.create(order, user);
        // when
        payment.applyCoupon(couponItem);

        // then
        assertThat(payment)
            .extracting(Payment::getUser, Payment::getOrder, Payment::getPaymentAmount)
            .containsExactly(user, order, order.getTotalAmount() - coupon.getDiscountAmount());
    }

    @Test
    void 이미_사용한_쿠폰을_적용하면_쿠폰_적용에_실패한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        Order order = Order.create(user, productQuantities);
        Coupon coupon = AmountCoupon.of("정액 쿠폰", 10, 1000);
        CouponItem couponItem = CouponItem.of(user, coupon, Boolean.TRUE);

        Payment payment = Payment.create(order, user);
        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> payment.applyCoupon(couponItem));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.COUPON_ALREADY_USED.getCode(), ErrorCode.COUPON_ALREADY_USED.getMessage());
    }

    @Test
    void 결제를_진행한다() {
        // given
        Integer userInitialAmount = 1_000_000;
        User user = User.of(1L, userInitialAmount);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        Order order = Order.create(user, productQuantities);
        Payment payment = Payment.create(order, user);

        // when
        payment.processPayment();

        // then
        assertThat(user.getAmount())
            .isEqualTo(userInitialAmount - payment.getPaymentAmount());

        assertThat(order.getStatus())
            .isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 결제금액보다_유저_잔액이_적은경우_결제를_실패한다() {
        // given
        Integer userInitialAmount = 1_000;
        User user = User.of(1L, userInitialAmount);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        Order order = Order.create(user, productQuantities);
        Payment payment = Payment.create(order, user);
        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> payment.processPayment());

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_BALANCE.getCode(), ErrorCode.INSUFFICIENT_BALANCE.getMessage());
    }
}
