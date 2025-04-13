package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderCommand.CreateOrder command) {
        if (command.getOrderUser() == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        if (command.getProductQuantities() == null
            || command.getProductQuantities().isEmpty()) {
            throw new BusinessLogicException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
        }

        Order order = Order.create(command.getOrderUser(), command.getProductQuantities(), command.getOrderAt());

        return orderRepository.save(order);
    }
}
