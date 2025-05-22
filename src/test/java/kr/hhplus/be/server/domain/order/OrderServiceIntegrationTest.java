package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 주문을_생성한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000_000));
        Product savedProduct = productRepository.save(Product.of(null, "상품", 10, 1_000));

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct, 1);

        OrderCommand.CreateOrder command = OrderCommand.CreateOrder.of(savedUser, productQuantities, LocalDateTime.now());

        // when
        Order order = orderService.createOrder(command);

        // then
        Assertions.assertThat(order)
            .extracting(Order::getTotalAmount, Order::getPaymentAmount, Order::getStatus)
            .containsExactly(1_000, null, OrderStatus.PAYMENT_PENDING);

        Assertions.assertThat(order.getOrderItems())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactlyInAnyOrder(
                tuple(savedProduct, 1)
            );
    }

    @Test
    void 주문을_조회한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000_000));
        Product savedProduct = productRepository.save(Product.of(null, "상품", 10, 1_000));

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct, 1);

        LocalDateTime orderAt = LocalDateTime.now();

        Order savedOrder = orderRepository.save(Order.create(savedUser, productQuantities, orderAt));

        // when
        Order resultOrder = orderService.getOrder(savedOrder.getId());

        // then
        assertThat(resultOrder)
            .extracting(Order::getUser, Order::getTotalAmount, Order::getOrderAt)
            .containsExactlyInAnyOrder(savedUser, savedOrder.getTotalAmount(), orderAt);
    }

    @Test
    void 없는_주문ID로_조회하면_ORDER_NOT_FOUND에러가_발생한다() {
        // given
        Long orderId = 1L;
        // when && then
        Assertions.assertThatThrownBy(() -> orderService.getOrder(orderId))
            .isInstanceOf(BusinessLogicException.class)
            .extracting("code", "message")
            .containsExactly(ErrorCode.ORDER_NOT_FOUND.getCode(), ErrorCode.ORDER_NOT_FOUND.getMessage());

    }
}
