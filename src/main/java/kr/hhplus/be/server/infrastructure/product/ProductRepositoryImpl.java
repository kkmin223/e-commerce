package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<Product> listProducts() {
        return productJpaRepository.findAll();
    }

    @Override
    public Optional<Product> getProduct(Long id) {
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
}
