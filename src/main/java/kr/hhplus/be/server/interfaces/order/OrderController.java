package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    @Override
    @PostMapping()
    public ResponseEntity<ApiResult<OrderResponse.Order>> order(OrderRequest.Order orderRequest) {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.ORDER, new OrderResponse.Order(1L, 10000, 5000, "2025-04-04 10:00", OrderStatus.COMPLETED)));

    }
}
