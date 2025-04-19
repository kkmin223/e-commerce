package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@SpringBootTest
public class ProductFacadeIntegrationTest {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private OrderStatisticsRepository orderStatisticsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 판매량이_많은_상품을_조회한다() {
        // given
        Product product1 = productRepository.save(Product.of(null, "상품1", 10, 1_000));
        Product product2 = productRepository.save(Product.of(null, "상품2", 10, 1_000));
        Product product3 = productRepository.save(Product.of(null, "상품3", 10, 1_000));
        Product product4 = productRepository.save(Product.of(null, "상품4", 10, 1_000));
        Product product5 = productRepository.save(Product.of(null, "상품5", 10, 1_000));
        Product product6 = productRepository.save(Product.of(null, "상품6", 10, 1_000));


        LocalDate date = LocalDate.now();
        Integer count = 5;
        ProductCriteria.GetTopSellingProducts criteria = ProductCriteria.GetTopSellingProducts.of(date.minusDays(3), date, count);
        orderStatisticsRepository.saveAll(List.of(
            OrderStatistics.of(date, product1, 100),
            OrderStatistics.of(date, product2, 200),
            OrderStatistics.of(date, product3, 300),
            OrderStatistics.of(date, product4, 400),
            OrderStatistics.of(date, product5, 500),
            OrderStatistics.of(date, product6, 600)));

        // when
        List<ProductResult.Product> result = productFacade.getTopSellingProducts(criteria);

        // then
        Assertions.assertThat(result)
            .hasSize(count)
            .extracting(ProductResult.Product::getId, ProductResult.Product::getName)
            .containsExactly(
                Tuple.tuple(product6.getId(), product6.getName()),
                Tuple.tuple(product5.getId(), product5.getName()),
                Tuple.tuple(product4.getId(), product4.getName()),
                Tuple.tuple(product3.getId(), product3.getName()),
                Tuple.tuple(product2.getId(), product2.getName())
            );
    }

}
