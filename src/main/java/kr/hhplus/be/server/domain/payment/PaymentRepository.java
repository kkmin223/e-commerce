package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrder(Order order);
}
