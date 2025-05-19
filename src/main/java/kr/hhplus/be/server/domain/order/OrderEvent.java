package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderEvent {

    private LocalDate orderDate;
    private List<ProductQuantity> productQuantities;

    public static OrderEvent of(LocalDate orderDate, Map<Product, Integer> productsWithQuantities) {
        return new OrderEvent(orderDate,
            productsWithQuantities
                .entrySet()
                .stream()
                .map(entry -> new ProductQuantity(entry.getKey().getId(), entry.getValue()))
                .toList());
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductQuantity {
        private Long productId;
        private Integer quantity;
    }
}
