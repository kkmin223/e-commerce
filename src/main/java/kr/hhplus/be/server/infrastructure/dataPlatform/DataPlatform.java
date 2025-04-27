package kr.hhplus.be.server.infrastructure.dataPlatform;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DataPlatform {

    @Async("taskExecutor")
    public void sendData(Order order){
        System.out.println("데이터 전송");
        return;
    }
}
