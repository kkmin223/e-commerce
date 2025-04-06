package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Facade
public class ProductFacade {

    private final ProductService productService;

    public List<ProductResult.Product> listProducts() {
        List<Product> products = productService.listProducts();
        return products.stream().map(ProductResult.Product::createdBy).toList();
    }

    public ProductResult.Product getProduct(ProductCriteria.Get criteria) {
        Product product = productService.getProduct(criteria.toCommand());
        return ProductResult.Product.createdBy(product);
    }
}
