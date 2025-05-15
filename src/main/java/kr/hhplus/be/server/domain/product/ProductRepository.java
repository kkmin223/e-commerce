package kr.hhplus.be.server.domain.product;

import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    List<Product> listProducts();

    Optional<Product> findById(Long id);

    List<Product> findAllByProductIds(List<Long> productIds);

    Product save(Product product);

    List<Product> findAllByProductIdsForUpdate(List<Long> productIds);

    void incrementProductScore(LocalDate orderAt, Long productId, Integer orderQuantity, Duration ttlDuration);

    Set<ZSetOperations.TypedTuple<String>> getProductSales(LocalDate searchDate);

    Set<ZSetOperations.TypedTuple<String>> getProductRanking(LocalDate startDate, LocalDate endDate, int rankCount);
}

