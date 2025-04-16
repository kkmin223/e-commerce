package kr.hhplus.be.server.infrastructure.orderItem;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.orderItem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> getCompletedWithinPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        return orderItemJpaRepository.findCompletedOrderItemsWithOrderAndProduct(OrderStatus.COMPLETED, startTime, endTime);
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }
}
