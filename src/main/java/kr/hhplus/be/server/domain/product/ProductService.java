package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.exceptions.InvalidProductIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> listProducts() {
        return productRepository.listProducts();
    }

    public Product getProduct(ProductCommand.Get getCommand) {
        if (getCommand.getProductId() <= 0) {
            throw new InvalidProductIdException();
        }

        return productRepository.getProduct(getCommand.getProductId());
    }
}
