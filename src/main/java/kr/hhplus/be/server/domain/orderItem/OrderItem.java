package kr.hhplus.be.server.domain.orderItem;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

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
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_STOCK);
        }

        return new OrderItem(product, quantity);
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
