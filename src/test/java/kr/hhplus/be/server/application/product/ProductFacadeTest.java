package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsService;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @InjectMocks
    private ProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private OrderStatisticsService orderStatisticsService;

    @Test
    void 판매량이_많은_상품을_조회한다() {
        // given
        ProductCriteria.GetTopSellingProducts criteria = ProductCriteria.GetTopSellingProducts.of(LocalDate.now().minusDays(3), LocalDate.now(), 5);

        // when
        List<ProductResult.Product> topSellingProducts = productFacade.getTopSellingProducts(criteria);

        // then
        InOrder inOrder = Mockito.inOrder(orderStatisticsService, productService);
        inOrder.verify(orderStatisticsService).getTopSellingProductIds(Mockito.any());
        inOrder.verify(productService).findTopSellingProduct(Mockito.any());

    }

}
