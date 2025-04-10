package kr.hhplus.be.server.domain.orderItem;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientStockException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItem {
    private Long id;
    private Product product;
    private Integer orderQuantity;
    private Integer subTotalAmount;

    private OrderItem(Product product, Integer orderQuantity) {
        this.product = product;
        this.orderQuantity = orderQuantity;
        this.subTotalAmount = product.getPrice() * orderQuantity;
        product.reduceQuantity(orderQuantity);
    }

    public static OrderItem create(Product product, Integer quantity) {
        if (!product.canOrder(quantity)) {
            throw new InsufficientStockException();
        }

        return new OrderItem(product, quantity);
    }
}
