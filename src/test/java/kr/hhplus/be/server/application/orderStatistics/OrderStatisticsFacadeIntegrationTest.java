package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.orderItem.OrderItemRepository;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
public class OrderStatisticsFacadeIntegrationTest {

    @Autowired
    private OrderStatisticsFacade orderStatisticsFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatisticsRepository orderStatisticsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void 날짜에_해당하는_상품_판매_통계를_생성한다() {
        // given
        LocalDate statisticDate = LocalDate.now();
        OrderStatisticsCriteria.GenerateStatisticsByDate criteria = OrderStatisticsCriteria.GenerateStatisticsByDate.of(statisticDate);

        User savedUser = userRepository.save(User.of(1_000_000));
        Product savedProduct = productRepository.save(Product.of(null, "상품", 10, 1_000));
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", 10, 1_000));

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct, 1);
        productQuantities.put(savedProduct2, 3);
        Order order = Order.create(savedUser, productQuantities, statisticDate.atTime(1, 0));
        order.completeOrder(4_000);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(savedOrder.getOrderItems());

        // when
        orderStatisticsFacade.generateStatisticsByDate(criteria);

        // then
        List<OrderStatistics> orderStatistics = orderStatisticsRepository.findByStatisticDate(statisticDate);

        Assertions.assertThat(orderStatistics)
            .hasSize(2)
            .extracting(OrderStatistics::getProductId, OrderStatistics::getSoldQuantity, OrderStatistics::getStatisticDate)
            .containsExactlyInAnyOrder(
                Tuple.tuple(savedProduct.getId(), 1, statisticDate),
                Tuple.tuple(savedProduct2.getId(), 3, statisticDate)
            );
    }
}
