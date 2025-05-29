package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.infrastructure.dataPlatform.DataPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderEventListener {

    private final OrderService orderService;
    private final ProductService productService;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(OrderEvent orderEvent) {
        Order order = orderService.getOrder(orderEvent.getOrderId());

        List<ProductCommand.IncreaseProductScore.ProductQuantity> productQuantities = order
            .getOrderItems()
            .stream()
            .map(orderItem -> ProductCommand.IncreaseProductScore.ProductQuantity.of(orderItem.getProduct().getId(), orderItem.getOrderQuantity()))
            .toList();
        ProductCommand.IncreaseProductScore increaseProductScoreCommand = ProductCommand.IncreaseProductScore.of(order.getOrderAt().toLocalDate()
            , productQuantities);
        productService.increaseProductScore(increaseProductScoreCommand);

        kafkaTemplate.send("ORDER", "DATA_PLATFORM", orderEvent);
    }
}
