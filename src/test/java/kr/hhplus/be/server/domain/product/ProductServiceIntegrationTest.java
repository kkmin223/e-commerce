package kr.hhplus.be.server.domain.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
@SpringBootTest
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품_리스트를_조회한다() {
        // given
        Product savedProduct1 = productRepository.save(Product.of(null, "상품1", 10, 10_000));
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", 10, 20_000));

        // when
        List<Product> products = productService.listProducts();

        // then
        assertThat(products)
            .extracting(Product::getId, Product::getName, Product::getPrice, Product::getQuantity)
            .containsExactlyInAnyOrder(
                tuple(savedProduct1.getId(), savedProduct1.getName(), savedProduct1.getPrice(), savedProduct1.getQuantity()),
                tuple(savedProduct2.getId(), savedProduct2.getName(), savedProduct2.getPrice(), savedProduct2.getQuantity())
            );
    }

    @Test
    void 상품_단건을_조회한다() {
        // given
        Product savedProduct1 = productRepository.save(Product.of(null, "상품1", 10, 10_000));
        ProductCommand.Get command = ProductCommand.Get.of(savedProduct1.getId());

        // when
        Product product = productService.getProduct(command);

        // then
        assertThat(product)
            .extracting(Product::getId, Product::getName, Product::getPrice, Product::getQuantity)
            .containsExactly(savedProduct1.getId(), savedProduct1.getName(), savedProduct1.getPrice(), savedProduct1.getQuantity());
    }

}
