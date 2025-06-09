package kr.hhplus.be.server.application.dataPlatform;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.infrastructure.dataPlatform.DataPlatform;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Facade
public class DataPlatformFacade {

    private final OrderService orderService;
    private final DataPlatform dataPlatform;

    public void sendData(OrderEvent orderEvent) {
        Order order = orderService.getOrder(orderEvent.getOrderId());
        dataPlatform.sendData(order);
    }
}
