package kr.hhplus.be.server.infrastructure.orderItem;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
        select oi from OrderItem oi
        join fetch oi.order o
        join fetch oi.product p
        where o.status = :status
        and o.orderAt between :start and :end
        """)
    List<OrderItem> findCompletedOrderItemsWithOrderAndProduct(
        @Param("status") OrderStatus status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    List<OrderItem> findByOrderId(Long orderId);
}
