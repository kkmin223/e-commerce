package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public List<Product> listProducts() {
        return List.of();
    }

    @Override
    public Product getProduct(Long id) {
        return null;
    }
}
