package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> listProducts() {
        return productRepository.listProducts();
    }

    public Product getProduct(ProductCommand.Get command) {
        if (command.getProductId() == null
            || command.getProductId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_PRODUCT_ID);
        }

        return productRepository.findById(command.getProductId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Map<Product, Integer> findProductsWithQuantities(ProductCommand.FindProductsWithQuantity command) {

        Map<Long, Integer> quantityMap = command.getProducts().stream()
            .peek(product -> {
                if (product.getProductId() == null
                    || product.getProductId() <= 0) {
                    throw new BusinessLogicException(ErrorCode.INVALID_PRODUCT_ID);
                }

                if (product.getQuantity() == null
                    || product.getQuantity() <= 0) {
                    throw new BusinessLogicException(ErrorCode.INVALID_ORDER_QUANTITY);
                }
            })
            .collect(Collectors.toMap(
                ProductCommand.ProductsWithQuantity::getProductId,
                ProductCommand.ProductsWithQuantity::getQuantity,
                Integer::sum // 같은 productId면 수량 합산
            ));

        List<Product> products = productRepository.findAllByProductIds(quantityMap.keySet().stream().toList());

        if (products.size() != quantityMap.size()) {
            throw new BusinessLogicException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
        }

        return products.stream()
            .collect(Collectors.toMap(
                product -> product,
                product -> quantityMap.get(product.getId())
            ));
    }

    public List<Product> findTopSellingProduct(ProductCommand.FindTopSellingProduct command) {
        List<Product> products = productRepository.findAllByProductIds(command.getProductIds());

        Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        return command.getProductIds()
            .stream()
            .map(productMap::get)
            .toList();
    }

    public List<ProductInfo.ProductSalesInfo> getProductSalesInfo(ProductCommand.GetProductSalesInfo command) {
        Set<ZSetOperations.TypedTuple<String>> productSales = productRepository.getProductSales(command.getSearchDate());

        return productSales.stream()
            .map(ProductInfo.ProductSalesInfo::of)
            .toList();
    }

    @Cacheable(value = "TopProduct", key = "{#command.startDate, #command.endDate, #command.rankCount}")
    public List<ProductInfo.ProductRanking> getTopProduct(ProductCommand.GetTopProduct command) {
        Set<ZSetOperations.TypedTuple<String>> productRankings = productRepository.getProductRanking(command.getStartDate(), command.getEndDate(), command.getRankCount());

        List<Long> productIds = productRankings.stream()
            .map(productRanking -> Long.valueOf(productRanking.getValue()))
            .toList();

        List<Product> products = productRepository.findAllByProductIds(productIds);

        List<ProductInfo.ProductRanking> result = new ArrayList<>();
        int ranking = 1;
        for (ZSetOperations.TypedTuple<String> productRanking : productRankings) {
            Long productId = Long.valueOf(productRanking.getValue());
            Integer soldQuantity = productRanking.getScore().intValue();

            Product product = products.stream()
                .filter(p -> productId.equals(p.getId()))
                .findFirst()
                .get();

            result.add(ProductInfo.ProductRanking.of(ranking, product.getId(), product.getName(), soldQuantity));
        }

        return result;
    }

    public void increaseProductScore(ProductCommand.IncreaseProductScore command) {
        for (ProductCommand.IncreaseProductScore.ProductQuantity productQuantity : command.getProductQuantities()) {
            productRepository.incrementProductScore(command.getOrderAt(), productQuantity.getProductId(), productQuantity.getQuantity(), Duration.ofHours(24 * 3 + 2));
        }
    }
}
