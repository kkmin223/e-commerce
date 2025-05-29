package kr.hhplus.be.server.interfaces.dataPlatform;

import kr.hhplus.be.server.application.dataPlatform.DataPlatformFacade;
import kr.hhplus.be.server.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataPlatformEventConsumer {

    private final DataPlatformFacade dataPlatformFacade;

    @KafkaListener(topics = "ORDER", groupId = "data-platform-group")
    public void handleDataPlatformEvent(ConsumerRecord<String, OrderEvent> record,
                                        Acknowledgment acknowledgment) {
        if ("DATA_PLATFORM".equals(record.key())) {
            dataPlatformFacade.sendData(record.value());
            // 메시지 처리가 완료되면 수동으로 오프셋 커밋
            acknowledgment.acknowledge();
        }
    }
}
