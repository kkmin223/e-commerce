package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.exceptions.InvalidProductIdException;
import kr.hhplus.be.server.interfaces.common.exceptions.OrderProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> listProducts() {
        return productRepository.listProducts();
    }

    public Product getProduct(ProductCommand.Get command) {
        if (command.getProductId() <= 0) {
            throw new InvalidProductIdException();
        }

        return productRepository.getProduct(command.getProductId());
    }

    public Map<Product, Integer> findProductsWithQuantities(ProductCommand.FindProductsWithQuantity command) {
        Map<Long, Integer> quantityMap = command.getProducts().stream()
            .collect(Collectors.toMap(
                ProductCommand.ProductsWithQuantity::getProductId,
                ProductCommand.ProductsWithQuantity::getQuantity,
                Integer::sum // 같은 productId면 수량 합산
            ));


        List<Product> products = productRepository.findAllByProductIds(quantityMap.keySet().stream().toList());

        if (products.size() != quantityMap.size()) {
            throw new OrderProductNotFoundException();
        }

        return products.stream()
            .collect(Collectors.toMap(
                product -> product,
                product -> quantityMap.get(product.getId())
            ));
    }
}
