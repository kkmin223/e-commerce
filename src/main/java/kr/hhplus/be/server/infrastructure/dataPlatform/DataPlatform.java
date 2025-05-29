package kr.hhplus.be.server.infrastructure.dataPlatform;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DataPlatform {

    public void sendData(Order order){
        System.out.println(String.format("orderID:%d 데이터 전송", order.getId()));
        return;
    }
}
