package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface ProductRepository {
    List<Product> listProducts();

    Product getProduct(Long id);

    List<Product> findAllByProductIds(List<Long> productIds);
}
