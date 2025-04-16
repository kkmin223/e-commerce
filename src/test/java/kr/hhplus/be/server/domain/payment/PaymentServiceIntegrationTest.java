package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Test
    void 결제를_생성하고_진행한다() {
        // given
        User savedUser = userRepository.save(User.of( 1_000_000));
        Integer stockQuantity1 = 10;
        Product savedProduct1 = productRepository.save(Product.of(null, "상품1", stockQuantity1, 10_000));
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", stockQuantity2, 20_000));
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct1, orderQuantity1);
        productQuantities.put(savedProduct2, orderQuantity2);

        Order savedOrder = orderRepository.save(Order.create(savedUser, productQuantities, LocalDateTime.now()));

        PaymentCommand.CreateAndProcess command = PaymentCommand.CreateAndProcess.of(savedUser, savedOrder, null);

        // when
        Payment result = paymentService.createAndProcess(command);

        // then
        assertThat(savedOrder.getStatus())
            .isEqualTo(OrderStatus.COMPLETED);

        assertThat(result)
            .extracting(Payment::getOrder, Payment::getUser, Payment::getPaymentAmount)
            .containsExactly(savedOrder, savedUser, savedOrder.getPaymentAmount());
    }

    @Test
    void 결제를_생성하고_쿠폰을_적용하여_진행한다() {
        // given
        User savedUser = userRepository.save(User.of( 1_000_000));
        Integer stockQuantity1 = 10;
        Product savedProduct1 = productRepository.save(Product.of(null, "상품1", stockQuantity1, 10_000));
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", stockQuantity2, 20_000));
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct1, orderQuantity1);
        productQuantities.put(savedProduct2, orderQuantity2);

        Order savedOrder = orderRepository.save(Order.create(savedUser, productQuantities, LocalDateTime.now()));

        Coupon savedCoupon = couponRepository.save(AmountCoupon.of("쿠폰", 10, 1_000));
        CouponItem savedCouponItem = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));

        PaymentCommand.CreateAndProcess command = PaymentCommand.CreateAndProcess.of(savedUser, savedOrder, savedCouponItem);

        // when
        Payment result = paymentService.createAndProcess(command);

        // then
        assertThat(savedOrder.getStatus())
            .isEqualTo(OrderStatus.COMPLETED);

        assertThat(result)
            .extracting(Payment::getOrder, Payment::getUser, Payment::getPaymentAmount)
            .containsExactly(savedOrder, savedUser, savedOrder.getPaymentAmount());
    }

}
