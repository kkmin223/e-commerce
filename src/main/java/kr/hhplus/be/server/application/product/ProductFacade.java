package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Facade
public class ProductFacade {

    private final OrderStatisticsService orderStatisticsService;
    private final ProductService productService;

    public List<ProductResult.Product> getTopSellingProducts(ProductCriteria.GetTopSellingProducts criteria) {
        List<Long> topSellingProductIds = orderStatisticsService.getTopSellingProductIds(OrderStatisticsCommand.GetTopSellingProductIds.of(criteria.getCount(), criteria.getStartDate(), criteria.getEndDate()));
        List<Product> topSellingProduct = productService.findTopSellingProduct(ProductCommand.FindTopSellingProduct.of(topSellingProductIds));

        return topSellingProduct
            .stream()
            .map(product -> ProductResult.Product.of(product.getId(), product.getName(), product.getPrice(), product.getQuantity()))
            .toList();
    }

}
