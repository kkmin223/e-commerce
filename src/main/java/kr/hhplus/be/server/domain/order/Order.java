package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class Order {
    private Long id;
    private Integer totalAmount;
    private Integer paymentAmount;
    private OrderStatus status;
    private LocalDateTime orderAt;
    private User user;
    List<OrderItem> orderItems;

    private Order(User user, List<OrderItem> orderItems, LocalDateTime orderAt) {
        this.user = user;
        this.orderItems = orderItems;
        this.totalAmount = calculateTotalAmount();
        this.paymentAmount = null;
        this.status = OrderStatus.PAYMENT_PENDING;
        this.orderAt = orderAt;
    }

    public static Order create(User user, Map<Product, Integer> productQuantities, LocalDateTime orderAt) {
        if (user == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        if (productQuantities == null
            || productQuantities.isEmpty()) {
            throw new BusinessLogicException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
        }

        List<OrderItem> orderItems = productQuantities.entrySet().stream()
            .map(entry -> OrderItem.create(entry.getKey(), entry.getValue()))
            .toList();

        return new Order(user, orderItems, orderAt);
    }

    private Integer calculateTotalAmount() {
        return this.orderItems
            .stream()
            .mapToInt(OrderItem::getSubTotalAmount)
            .sum();
    }

    public void completeOrder(Integer paymentAmount) {
        this.paymentAmount = paymentAmount;
        this.status = OrderStatus.COMPLETED;
    }

}
