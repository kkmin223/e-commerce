package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@SpringBootTest
class OrderStatisticsRepositoryImplTest {

    @Autowired
    private OrderStatisticsRepositoryImpl orderStatisticsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 기간에_판매량이_가장_높은_ProductId를_조회한다() {
        // given
        LocalDate day1 = LocalDate.now();
        LocalDate day2 = day1.minusDays(1);
        Product product1 = productRepository.save(Product.of(null, "상품1", 10, 10_000));
        Product product2 = productRepository.save(Product.of(null, "상품2", 10, 10_000));
        Product product3 = productRepository.save(Product.of(null, "상품3", 10, 10_000));
        Product product4 = productRepository.save(Product.of(null, "상품4", 10, 10_000));
        Product product5 = productRepository.save(Product.of(null, "상품5", 10, 10_000));


        List<OrderStatistics> orderStatistics = List.of(
            // product1 -> 2000
            OrderStatistics.of(day1, product1, 1000),
            OrderStatistics.of(day2, product1, 1000),

            // product2 -> 2200
            OrderStatistics.of(day1, product2, 900),
            OrderStatistics.of(day2, product2, 1300),

            // product3 -> 1600
            OrderStatistics.of(day1, product3, 800),
            OrderStatistics.of(day2, product3, 800),

            // product4 -> 1400
            OrderStatistics.of(day1, product4, 700),
            OrderStatistics.of(day2, product4, 700),

            // product5 -> 1200
            OrderStatistics.of(day1, product5, 600),
            OrderStatistics.of(day2, product5, 600)
        );

        orderStatisticsRepository.saveAll(orderStatistics);

        // when
        List<Long> topSellingProductIds = orderStatisticsRepository.findTopSellingProductIds(day2, day1, 1);

        // then
        Assertions.assertThat(topSellingProductIds)
            .hasSize(1)
            .containsExactly(product2.getId());
    }

    @Test
    void 가장_기간_판매량이_높은_상품이_중복되면_더_작은_ProductId를_조회한다() {
        // given
        LocalDate day1 = LocalDate.now();
        LocalDate day2 = day1.minusDays(1);
        Product product1 = productRepository.save(Product.of(null, "상품1", 10, 10_000));
        Product product2 = productRepository.save(Product.of(null, "상품2", 10, 10_000));
        Product product3 = productRepository.save(Product.of(null, "상품3", 10, 10_000));
        Product product4 = productRepository.save(Product.of(null, "상품4", 10, 10_000));
        Product product5 = productRepository.save(Product.of(null, "상품5", 10, 10_000));


        List<OrderStatistics> orderStatistics = List.of(
            // product1 -> 2200
            OrderStatistics.of(day1, product1, 1000),
            OrderStatistics.of(day2, product1, 1200),

            // product2 -> 2200
            OrderStatistics.of(day1, product2, 900),
            OrderStatistics.of(day2, product2, 1300),

            // product3 -> 1600
            OrderStatistics.of(day1, product3, 800),
            OrderStatistics.of(day2, product3, 800),

            // product4 -> 1400
            OrderStatistics.of(day1, product4, 700),
            OrderStatistics.of(day2, product4, 700),

            // product5 -> 1200
            OrderStatistics.of(day1, product5, 600),
            OrderStatistics.of(day2, product5, 600)
        );

        orderStatisticsRepository.saveAll(orderStatistics);

        // when
        List<Long> topSellingProductIds = orderStatisticsRepository.findTopSellingProductIds(day2, day1, 1);

        // then
        Assertions.assertThat(topSellingProductIds)
            .hasSize(1)
            .containsExactly(product1.getId());
    }

}
