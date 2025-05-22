package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.orderItem.OrderItemRepository;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Order createOrder(OrderCommand.CreateOrder command) {
        if (command.getOrderUser() == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        if (command.getProductQuantities() == null
            || command.getProductQuantities().isEmpty()) {
            throw new BusinessLogicException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
        }

        Order savedOrder = orderRepository.save(Order.create(command.getOrderUser(), command.getProductQuantities(), command.getOrderAt()));
        orderItemRepository.saveAll(savedOrder.getOrderItems());

        return savedOrder;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.ORDER_NOT_FOUND));
    }
}
