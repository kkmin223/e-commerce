package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
}
