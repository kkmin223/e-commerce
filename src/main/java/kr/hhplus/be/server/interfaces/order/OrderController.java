package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping()
    public ResponseEntity<ApiResult<OrderResponse.Order>> order(@RequestBody OrderRequest.Order orderRequest) {
        OrderResult.OrderAndPay orderAndPay = orderFacade.orderAndPay(orderRequest.toCriteria(LocalDateTime.now()));
        return ResponseEntity.ok(ApiResult.of(SuccessCode.ORDER, OrderResponse.Order.createdBy(orderAndPay)));
    }
}
