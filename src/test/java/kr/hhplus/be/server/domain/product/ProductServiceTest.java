package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidProductIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void 상품_리스트를_조회한다() {
        // given
        List<Product> products = List.of(
            Product.of(1L, "상품", 10, 10_000),
            Product.of(2L, "상품2", 10, 10_000)
        );

        when(productRepository.listProducts()).thenReturn(products);
        // when
        List<Product> resultProducts = productService.listProducts();

        // then
        assertThat(resultProducts)
            .hasSize(products.size())
            .isEqualTo(products);
    }

    @Test
    void 단일_상품을_조회한다() {
        // given
        Product product = Product.of(1L, "상품", 10, 10_000);

        ProductCommand.Get getCommand = new ProductCommand.Get(product.getId());

        when(productRepository.getProduct(getCommand.getProductId())).thenReturn(product);
        // when
        Product resultProduct = productService.getProduct(getCommand);

        // then
        assertThat(resultProduct)
            .isEqualTo(product);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0})
    void 상품_식별자가_0보다_작거나_같을때_상품_조회가_실패한다(Long productId) {
        // given
        ProductCommand.Get getCommand = new ProductCommand.Get(productId);

        // when
        InvalidProductIdException exception = assertThrows(InvalidProductIdException.class, () -> productService.getProduct(getCommand));

        // then
        assertThat(exception)
            .extracting(InvalidProductIdException::getCode, InvalidProductIdException::getMessage)
            .containsExactly(ErrorCode.INVALID_PRODUCT_ID.getCode(), ErrorCode.INVALID_PRODUCT_ID.getMessage());

    }
}
