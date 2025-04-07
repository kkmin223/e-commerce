package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Long productId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindProductsWithQuantity {

        private List<ProductsWithQuantity> products;

        public static FindProductsWithQuantity of(List<OrderCriteria.OrderProduct> products) {
            return new FindProductsWithQuantity(products.stream().map(ProductsWithQuantity::of).toList());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductsWithQuantity {
        private Long productId;
        private Integer quantity;

        public static ProductsWithQuantity of(OrderCriteria.OrderProduct product) {
            return new ProductsWithQuantity(product.getProductId(), product.getQuantity());
        }
    }
}
