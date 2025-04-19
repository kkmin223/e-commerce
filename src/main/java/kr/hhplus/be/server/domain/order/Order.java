package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
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
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer totalAmount;

    private Integer paymentAmount;

    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToMany(mappedBy = "order")
    List<OrderItem> orderItems;

    @Column(nullable = false)
    private LocalDateTime orderAt;

    private Order(User user, List<OrderItem> orderItems, LocalDateTime orderAt) {
        this.user = user;
        this.orderItems = orderItems;
        this.totalAmount = calculateTotalAmount();
        this.paymentAmount = null;
        this.status = OrderStatus.PAYMENT_PENDING;
        this.orderAt = orderAt;

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(this); // 여기서 역방향 연관관계 세팅
        }

    }

    public static Order create(User user, Map<Product, Integer> productQuantities, LocalDateTime orderAt) {
        if (user == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        if (productQuantities == null
            || productQuantities.isEmpty()) {
            throw new BusinessLogicException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
        }

        if (orderAt == null) {
            orderAt = LocalDateTime.now();
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

    public boolean canPay() {
        return this.status == OrderStatus.PAYMENT_PENDING;
    }

}
