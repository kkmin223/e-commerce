package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @InjectMocks
    private ProductFacade productFacade;

    @Mock
    private ProductService productService;


    @Test
    void 상품_리스트를_조회한다() {
        // given
        List<Product> products = List.of(
            Product.of(1L, "상품", 10, 10_000),
            Product.of(2L, "상품2", 10, 10_000)
        );

        Mockito.when(productService.listProducts()).thenReturn(products);
        // when
        List<ProductResult.Product> resultProducts = productFacade.listProducts();

        // then
        assertThat(resultProducts)
            .hasSize(2)
            .extracting(ProductResult.Product::getId, ProductResult.Product::getName, ProductResult.Product::getQuantity, ProductResult.Product::getPrice)
            .containsExactlyInAnyOrder(
                tuple(products.get(0).getId(), products.get(0).getName(), products.get(0).getQuantity(), products.get(0).getPrice()),
                tuple(products.get(1).getId(), products.get(1).getName(), products.get(1).getQuantity(), products.get(1).getPrice())
            );

    }

    @Test
    void 상품_단건을_조회한다() {
        // given
        Product product = Product.of(1L, "상품", 10, 10_000);
        ProductCriteria.Get get = new ProductCriteria.Get(product.getId());

        Mockito.when(productService.getProduct(any(ProductCommand.Get.class))).thenReturn(product);
        // when
        ProductResult.Product resultProduct = productFacade.getProduct(get);

        // then
        assertThat(resultProduct)
            .extracting(ProductResult.Product::getId, ProductResult.Product::getName, ProductResult.Product::getQuantity, ProductResult.Product::getPrice)
            .containsExactly(product.getId(), product.getName(), product.getQuantity(), product.getPrice());
    }

}
