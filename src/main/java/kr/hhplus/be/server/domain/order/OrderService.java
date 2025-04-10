package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.interfaces.common.exceptions.OrderProductNotFoundException;
import kr.hhplus.be.server.interfaces.common.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderCommand.CreateOrder command) {
        if (command.getOrderUser() == null) {
            throw new UserNotFoundException();
        }

        if (command.getProductQuantities() == null
            || command.getProductQuantities().isEmpty()) {
            throw new OrderProductNotFoundException();
        }

        Order order = Order.create(command.getOrderUser(), command.getProductQuantities(), command.getOrderAt());

        return orderRepository.save(order);
    }
}
