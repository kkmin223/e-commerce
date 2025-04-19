package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
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

@Transactional
@SpringBootTest
public class OrderStatisticsServiceIntegrationTest {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatisticsRepository orderStatisticsRepository;

    @Test
    void 날짜를_입력받아_상품_통계를_생성한다() {
        // given
        LocalDate statisticDate = LocalDate.now();
        User savedUser = userRepository.save(User.of(1_000_000));
        Product savedProduct = productRepository.save(Product.of(null, "상품", 10, 1_000));
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", 10, 1_000));

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(savedProduct, 1);
        productQuantities.put(savedProduct2, 3);
        Order savedOrder = orderRepository.save(Order.create(savedUser, productQuantities, statisticDate.atStartOfDay()));
        OrderStatisticsCommand.GenerateDailyStatistics command = OrderStatisticsCommand.GenerateDailyStatistics.of(statisticDate, savedOrder.getOrderItems());

        // when
        orderStatisticsService.generateDailyStatistics(command);

        // then
        List<OrderStatistics> expected = orderStatisticsRepository.findByStatisticDate(statisticDate);

        Assertions.assertThat(expected)
            .hasSize(2)
            .extracting(OrderStatistics::getStatisticDate, OrderStatistics::getProductId, OrderStatistics::getSoldQuantity)
            .containsExactlyInAnyOrder(
                Tuple.tuple(statisticDate, savedProduct.getId(), 1),
                Tuple.tuple(statisticDate, savedProduct2.getId(), 3)
            );

    }
}
