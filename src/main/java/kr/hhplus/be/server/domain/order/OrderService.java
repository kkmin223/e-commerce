package kr.hhplus.be.server.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderCommand.CreateOrder command) {
        Order order = Order.create(command.getOrderUser(), command.getProductQuantities(), command.getOrderAt());

        return orderRepository.save(order);
    }
}
