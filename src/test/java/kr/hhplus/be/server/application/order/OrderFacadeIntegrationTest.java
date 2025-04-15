package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void 주문을_생성하고_쿠폰을_사용하여_결재를_진행한다() {
        // given
        Integer initialAmount = 1_000_000;
        Integer initialQuantity = 10;

        User savedUser = userRepository.save(User.of(initialAmount));
        Product savedProduct = productRepository.save(Product.of(null, "상품", initialQuantity, 1_000));
        Coupon savedCoupon = couponRepository.save(AmountCoupon.of("쿠폰", 1, 100));
        CouponItem savedCouponItem = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, false));

        OrderCriteria.OrderProduct orderProduct = OrderCriteria.OrderProduct.of(savedProduct.getId(), 1);
        OrderCriteria.OrderAndPay criteria = OrderCriteria.OrderAndPay.of(savedUser.getId(), savedCouponItem.getId(), List.of(orderProduct));

        // when
        OrderResult.OrderAndPay result = orderFacade.orderAndPay(criteria);

        // then
        Integer expectedTotalAmount = 1_000;
        Integer expectedPayAmount = 900;

        Integer expectedUserAmount = initialAmount - expectedPayAmount;

        // 결과 검증
        assertThat(result)
            .extracting(OrderResult.OrderAndPay::getTotalAmount, OrderResult.OrderAndPay::getPaymentAmount, OrderResult.OrderAndPay::getStatus)
            .containsExactly(expectedTotalAmount, expectedPayAmount, OrderStatus.COMPLETED);

        // 쿠폰 검증
        assertThat(savedCouponItem.getIsUsed())
            .isTrue();

        // 주문 검증
        Order expectedOrder = orderRepository.getOrder(result.getOrderId())
            .orElseThrow();

        assertThat(expectedOrder)
            .extracting(Order::getTotalAmount, Order::getPaymentAmount, Order::getStatus)
            .containsExactly(expectedTotalAmount, expectedPayAmount, OrderStatus.COMPLETED);

        // 유저 검증
        assertThat(savedUser.getAmount())
            .isEqualTo(expectedUserAmount);

        assertThat(expectedOrder.getOrderItems())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactly(Tuple.tuple(savedProduct, 1));

        // 결제 검증
        Payment payment = paymentRepository.findByOrder(expectedOrder).orElseThrow();

        assertThat(payment)
            .extracting(Payment::getUser, Payment::getPaymentAmount, Payment::getCouponItem)
            .containsExactly(savedUser, expectedPayAmount, savedCouponItem);

        // 상품 검증
        assertThat(savedProduct.getQuantity())
            .isEqualTo(initialQuantity - 1);

    }

    @Test
    void 주문을_생성하고_쿠폰을_사용하지않고_결재를_진행한다() {
        // given
        Integer initialAmount = 1_000_000;
        Integer initialQuantity = 10;

        User savedUser = userRepository.save(User.of(initialAmount));
        Product savedProduct = productRepository.save(Product.of(null, "상품", initialQuantity, 1_000));

        OrderCriteria.OrderProduct orderProduct = OrderCriteria.OrderProduct.of(savedProduct.getId(), 1);
        OrderCriteria.OrderAndPay criteria = OrderCriteria.OrderAndPay.of(savedUser.getId(), null, List.of(orderProduct));

        // when
        OrderResult.OrderAndPay result = orderFacade.orderAndPay(criteria);

        // then
        Integer expectedTotalAmount = 1_000;
        Integer expectedPayAmount = 1_000;

        Integer expectedUserAmount = initialAmount - expectedPayAmount;

        // 결과 검증
        assertThat(result)
            .extracting(OrderResult.OrderAndPay::getTotalAmount, OrderResult.OrderAndPay::getPaymentAmount, OrderResult.OrderAndPay::getStatus)
            .containsExactly(expectedTotalAmount, expectedPayAmount, OrderStatus.COMPLETED);

        // 주문 검증
        Order expectedOrder = orderRepository.getOrder(result.getOrderId())
            .orElseThrow();

        assertThat(expectedOrder)
            .extracting(Order::getTotalAmount, Order::getPaymentAmount, Order::getStatus)
            .containsExactly(expectedTotalAmount, expectedPayAmount, OrderStatus.COMPLETED);

        // 유저 검증
        assertThat(savedUser.getAmount())
            .isEqualTo(expectedUserAmount);

        assertThat(expectedOrder.getOrderItems())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactly(Tuple.tuple(savedProduct, 1));

        // 결제 검증
        Payment payment = paymentRepository.findByOrder(expectedOrder).orElseThrow();

        assertThat(payment)
            .extracting(Payment::getUser, Payment::getPaymentAmount, Payment::getCouponItem)
            .containsExactly(savedUser, expectedPayAmount, null);

        // 상품 검증
        assertThat(savedProduct.getQuantity())
            .isEqualTo(initialQuantity - 1);
    }
}
