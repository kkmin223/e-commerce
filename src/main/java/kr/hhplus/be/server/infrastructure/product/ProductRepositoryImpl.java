package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductRedisRepository productRedisRepository;

    @Override
    public List<Product> listProducts() {
        return productJpaRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAllByProductIds(List<Long> productIds) {
        return productJpaRepository.findAllById(productIds);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findAllByProductIdsForUpdate(List<Long> productIds) {
        return productJpaRepository.findAllByProductIdsForUpdate(productIds);
    }

    @Override
    public void incrementProductScore(LocalDate orderAt, Long productId, Integer orderQuantity, Duration ttlDuration) {
        productRedisRepository.incrementProductDailyScore(orderAt, productId, orderQuantity, ttlDuration);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getProductSales(LocalDate searchDate) {
        return productRedisRepository.getProductSales(searchDate);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getProductRanking(LocalDate startDate, LocalDate endDate, int rankCount) {
        return productRedisRepository.getProductRanking(startDate, endDate, rankCount);
    }


}
