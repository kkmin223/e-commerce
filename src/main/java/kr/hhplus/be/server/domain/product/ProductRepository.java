package kr.hhplus.be.server.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> listProducts();

    Optional<Product> findById(Long id);

    List<Product> findAllByProductIds(List<Long> productIds);

    Product save(Product product);
}
