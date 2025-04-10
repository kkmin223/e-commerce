package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void 결제를_생성하고_진행한다() {
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

        LocalDateTime orderAt = LocalDateTime.now();

        Order order = Order.create(user, productQuantities, orderAt);

        PaymentCommand.CreateAndProcess command = PaymentCommand.CreateAndProcess.of(user, order, null);

        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(Payment.create(order, user));

        // when
        Payment result = paymentService.createAndProcess(command);

        // then
        assertThat(order.getStatus())
            .isEqualTo(OrderStatus.COMPLETED);

        assertThat(result)
            .extracting(Payment::getOrder, Payment::getUser, Payment::getPaymentAmount)
            .containsExactly(order, user, order.getPaymentAmount());
    }

    void 결제를_생성하고_쿠폰을_적용하고_진행한다() {
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

        LocalDateTime orderAt = LocalDateTime.now();

        Order order = Order.create(user, productQuantities, orderAt);
        AmountCoupon coupon = AmountCoupon.of("정액 쿠폰", 10, 1000);
        CouponItem couponItem = CouponItem.of(user, coupon, Boolean.FALSE);

        PaymentCommand.CreateAndProcess command = PaymentCommand.CreateAndProcess.of(user, order, couponItem);

        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(Payment.create(order, user));

        // when
        Payment result = paymentService.createAndProcess(command);

        // then
        assertThat(order.getStatus())
            .isEqualTo(OrderStatus.COMPLETED);

        assertThat(result)
            .extracting(Payment::getOrder, Payment::getUser, Payment::getPaymentAmount)
            .containsExactly(order, user, order.getPaymentAmount());

        assertThat(couponItem.getIsUsed())
            .isEqualTo(Boolean.TRUE);
    }

    @Test
    void 유저_잔액이_부족할_때_결제를_생성하고_진행하면_결제가_실패한다() {
        // given
        User user = User.of(1L, 1_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        LocalDateTime orderAt = LocalDateTime.now();

        Order order = Order.create(user, productQuantities, orderAt);

        PaymentCommand.CreateAndProcess command = PaymentCommand.CreateAndProcess.of(user, order, null);

        // when
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> paymentService.createAndProcess(command));

        // then
        assertThat(exception)
            .extracting(InsufficientBalanceException::getCode, InsufficientBalanceException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_BALANCE.getCode(), ErrorCode.INSUFFICIENT_BALANCE.getMessage());

        Mockito.verify(paymentRepository, Mockito.never()).save(Mockito.any(Payment.class));
    }


}
