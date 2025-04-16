package kr.hhplus.be.server.domain.orderItem;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);

    List<OrderItem> getCompletedWithinPeriod(LocalDateTime startTime, LocalDateTime endTime);

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> saveAll(List<OrderItem> orderItems);
}
