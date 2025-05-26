package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.order.OrderCriteria;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ProductCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Get {
        private Long productId;

        public static Get of(Long productId) {
            return new Get(productId);
        }
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FindTopSellingProduct {
        private List<Long> productIds;

        public static FindTopSellingProduct of(List<Long> productIds) {
            return new FindTopSellingProduct(productIds);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetProductSalesInfo {
        private LocalDate searchDate;

        public static GetProductSalesInfo of(LocalDate searchDate) {
            return new GetProductSalesInfo(searchDate);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetTopProduct {
        private LocalDate startDate;
        private LocalDate endDate;
        private int rankCount;

        public static GetTopProduct of(LocalDate startDate, LocalDate endDate, int rankCount) {
            return new GetTopProduct(startDate, endDate, rankCount);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IncreaseProductScore {
        private LocalDate orderAt;
        private List<ProductQuantity> productQuantities;

        public static IncreaseProductScore of(LocalDate orderAt, List<ProductQuantity> productQuantities) {
            return new IncreaseProductScore(orderAt, productQuantities);
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductQuantity {
            private Long productId;
            private Integer quantity;

            public static ProductQuantity of(Long productId, Integer quantity) {
                return new ProductQuantity(productId, quantity);
            }
        }
    }
}
