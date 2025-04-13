package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public List<Product> listProducts() {
        return List.of();
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return null;
    }

    @Override
    public List<Product> findAllByProductIds(List<Long> productIds) {
        return List.of();
    }
}
