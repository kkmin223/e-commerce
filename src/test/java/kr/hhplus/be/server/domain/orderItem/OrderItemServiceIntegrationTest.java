package kr.hhplus.be.server.domain.orderItem;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
public class OrderItemServiceIntegrationTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 특정날에_완료된_주문내역을_조회한다() {
        // given
        LocalDate date = LocalDate.now();
        OrderItemCommand.GetCompletedByDate command = OrderItemCommand.GetCompletedByDate.of(date);

        User savedUser = userRepository.save(User.of(1_000_000));
        Product savedProduct = productRepository.save(Product.of(null, "상품", 10, 1_000));
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", 10, 1_000));

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct, 1);
        productQuantities.put(savedProduct2, 3);
        Order order = Order.create(savedUser, productQuantities, date.atTime(1, 0));
        order.completeOrder(4_000);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(savedOrder.getOrderItems());

        // when
        List<OrderItem> result = orderItemService.getCompletedByDate(command);

        // then
        Assertions.assertThat(result)
            .hasSize(2)
            .extracting(orderItem -> orderItem.getProduct().getId(), OrderItem::getOrderQuantity)
            .containsExactlyInAnyOrder(
                tuple(savedProduct.getId(), 1),
                tuple(savedProduct2.getId(), 3)
            );

    }
}
