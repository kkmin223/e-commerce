package kr.hhplus.be.server.domain.product;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
@SpringBootTest
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

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

    @Test
    void 상품_일별_판매량을_조회한다() {
        // given
        LocalDate searchDate = LocalDate.of(2024, 5, 15);
        Long productId = 1L;
        Integer orderQuantity = 3;

        String key = "PRODUCT:DAILY:" + searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value = productId.toString();

        redisTemplate.opsForZSet().incrementScore(key, value, orderQuantity.intValue());
        ProductCommand.GetProductSalesInfo command = ProductCommand.GetProductSalesInfo.of(searchDate);

        // when
        List<ProductInfo.ProductSalesInfo> productSalesInfo = productService.getProductSalesInfo(command);

        // then
        assertThat(productSalesInfo)
            .extracting(ProductInfo.ProductSalesInfo::getProductId, ProductInfo.ProductSalesInfo::getQuantity)
            .containsExactlyInAnyOrder(
                tuple(productId, orderQuantity)
            );
    }

    @Test
    void 기간으로_인기_상품을_조회한다() {
        // given
        LocalDate date1 = LocalDate.of(2025, 5, 13);
        LocalDate date2 = LocalDate.of(2025, 5, 14);
        LocalDate date3 = LocalDate.of(2025, 5, 15);
        Product savedProduct1 = productRepository.save(Product.of(null, "상품1", 10, 10_000));
        Product savedProduct2 = productRepository.save(Product.of(null, "상품2", 10, 20_000));
        Product savedProduct3 = productRepository.save(Product.of(null, "상품3", 10, 20_000));

        Long productId1 = savedProduct1.getId();
        Long productId2 = savedProduct2.getId();
        Long productId3 = savedProduct3.getId();
        Integer orderQuantity1 = 3;
        Integer orderQuantity2 = 5;
        Integer orderQuantity3 = 4;

        String key1 = "PRODUCT:DAILY:" + date1.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key2 = "PRODUCT:DAILY:" + date2.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key3 = "PRODUCT:DAILY:" + date3.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String value1 = productId1.toString();
        String value2 = productId2.toString();
        String value3 = productId3.toString();

        redisTemplate.opsForZSet().incrementScore(key1, value1, orderQuantity1.intValue());
        redisTemplate.opsForZSet().incrementScore(key2, value2, orderQuantity2.intValue());
        redisTemplate.opsForZSet().incrementScore(key3, value3, orderQuantity3.intValue());

        ProductCommand.GetTopProduct getTopProduct = ProductCommand.GetTopProduct.of(date1, date3, 1);

        // when
        List<ProductInfo.ProductRanking> topProduct = productService.getTopProduct(getTopProduct);

        // then
        assertThat(topProduct)
            .extracting(ProductInfo.ProductRanking::getProductId, ProductInfo.ProductRanking::getProductName, ProductInfo.ProductRanking::getRanking, ProductInfo.ProductRanking::getSoldQuantity)
            .containsExactlyInAnyOrder(
                tuple(savedProduct2.getId(), savedProduct2.getName(), 1, orderQuantity2)
            );
    }

    @Test
    void 상품_스코어를_증가시킨다() {
        // given
        LocalDate orderAt = LocalDate.now();
        long productId = 1L;
        int quantity = 3;
        ProductCommand.IncreaseProductScore.ProductQuantity productQuantity = ProductCommand.IncreaseProductScore.ProductQuantity.of(productId, quantity);
        ProductCommand.IncreaseProductScore command = ProductCommand.IncreaseProductScore.of(orderAt, List.of(productQuantity));

        // when
        productService.increaseProductScore(command);

        // then
        String value = String.valueOf(productId);
        String key = "PRODUCT:DAILY:" + orderAt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Set<ZSetOperations.TypedTuple<String>> expectedTuple = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        Assertions.assertThat(expectedTuple)
            .hasSize(1)
            .extracting(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore)
            .containsExactly(Tuple.tuple(value, Double.valueOf(quantity)));
    }
}
