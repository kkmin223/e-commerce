package kr.hhplus.be.server.infrastructure.orderItem;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
