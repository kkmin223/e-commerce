package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        ProductCommand.Get getCommand = ProductCommand.Get.of(product.getId());

        when(productRepository.findById(getCommand.getProductId())).thenReturn(Optional.of(product));
        // when
        Product resultProduct = productService.getProduct(getCommand);

        // then
        assertThat(resultProduct)
            .isEqualTo(product);
    }

    @Test
    void 상품_식별자가_null이면_상품_조회가_실패한다() {
        // given
        Long productId = null;
        ProductCommand.Get getCommand = ProductCommand.Get.of(productId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.getProduct(getCommand));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_PRODUCT_ID.getCode(), ErrorCode.INVALID_PRODUCT_ID.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0})
    void 상품_식별자가_0보다_작거나_같을때_상품_조회가_실패한다(Long productId) {
        // given
        ProductCommand.Get getCommand = ProductCommand.Get.of(productId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.getProduct(getCommand));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_PRODUCT_ID.getCode(), ErrorCode.INVALID_PRODUCT_ID.getMessage());

    }

    @Test
    void 주문요청을_상품_조회하고_상품_수량_Map을_반환한다() {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(1L, 2);
        ProductCommand.ProductsWithQuantity productsWithQuantity2 = new ProductCommand.ProductsWithQuantity(2L, 2);

        Product product1 = Product.of(1L, "상품1", 10, 10_000);
        Product product2 = Product.of(2L, "상품2", 10, 10_000);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1, productsWithQuantity2));

        when(productRepository.findAllByProductIdsForUpdate(List.of(productsWithQuantity1.getProductId(), productsWithQuantity2.getProductId()))).thenReturn(List.of(product1, product2));

        // when
        Map<Product, Integer> productsWithQuantities = productService.findProductsWithQuantities(command);

        // then
        assertThat(productsWithQuantities)
            .hasSize(2)
            .containsEntry(product1, productsWithQuantity1.getQuantity())
            .containsEntry(product2, productsWithQuantity2.getQuantity());
    }

    @Test
    void 주문요청에_동일한_상품ID가_있으면_상품_수량을_합친다() {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(1L, 2);
        ProductCommand.ProductsWithQuantity productsWithQuantity2 = new ProductCommand.ProductsWithQuantity(1L, 2);

        Product product1 = Product.of(1L, "상품1", 10, 10_000);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1, productsWithQuantity2));

        when(productRepository.findAllByProductIdsForUpdate(List.of(productsWithQuantity1.getProductId()))).thenReturn(List.of(product1));

        // when
        Map<Product, Integer> productsWithQuantities = productService.findProductsWithQuantities(command);

        // then
        assertThat(productsWithQuantities)
            .hasSize(1)
            .containsEntry(product1, productsWithQuantity1.getQuantity() + productsWithQuantity2.getQuantity());
    }

    @Test
    void 주문요청에_존재하지_않는_상품_식별자가_들어오면_에러가_발생한다() {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(1L, 2);
        ProductCommand.ProductsWithQuantity productsWithQuantity2 = new ProductCommand.ProductsWithQuantity(2L, 2);

        Product product1 = Product.of(1L, "상품1", 10, 10_000);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1, productsWithQuantity2));

        when(productRepository.findAllByProductIdsForUpdate(List.of(productsWithQuantity1.getProductId(), productsWithQuantity2.getProductId()))).thenReturn(List.of(product1));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.findProductsWithQuantities(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.ORDER_PRODUCT_NOT_FOUND.getCode(), ErrorCode.ORDER_PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void 주문요청에_상품_식별자가_null이면_에러가_발생한다() {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(null, 2);
        ProductCommand.ProductsWithQuantity productsWithQuantity2 = new ProductCommand.ProductsWithQuantity(2L, 2);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1, productsWithQuantity2));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.findProductsWithQuantities(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_PRODUCT_ID.getCode(), ErrorCode.INVALID_PRODUCT_ID.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void 주문요청에_상품_식별자가_0보다_작거나_같으면_에러가_발생한다(Long productId) {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(productId, 2);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.findProductsWithQuantities(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_PRODUCT_ID.getCode(), ErrorCode.INVALID_PRODUCT_ID.getMessage());
    }

    @Test
    void 주문요청에_주문수량이_null이면_에러가_발생한다() {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(1L, null);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.findProductsWithQuantities(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_ORDER_QUANTITY.getCode(), ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 주문요청에_주문수량이_0보다_작거나_같으면_에러가_발생한다(Integer quantity) {
        // given
        ProductCommand.ProductsWithQuantity productsWithQuantity1 = new ProductCommand.ProductsWithQuantity(1L, quantity);

        ProductCommand.FindProductsWithQuantity command = new ProductCommand.FindProductsWithQuantity(List.of(productsWithQuantity1));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> productService.findProductsWithQuantities(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_ORDER_QUANTITY.getCode(), ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
    }
}
