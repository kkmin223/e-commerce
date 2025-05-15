package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class ProductEventListener {

    private final ProductRepository productRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(OrderEvent orderEvent) {

        orderEvent.getProductQuantities().forEach(productQuantities -> {
            productRepository.incrementProductScore(orderEvent.getOrderDate(), productQuantities.getProductId(), productQuantities.getQuantity(), Duration.ofHours(24 * 3 + 2));
        });
    }

}

