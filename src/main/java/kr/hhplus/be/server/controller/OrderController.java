package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.api.OrderApi;
import kr.hhplus.be.server.domain.Order.OrderStatus;
import kr.hhplus.be.server.dto.OrderRequestDto;
import kr.hhplus.be.server.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {
    @Override
    public ResponseEntity<OrderResponseDto> order(OrderRequestDto couponIssueRequestDto) {
        return ResponseEntity.ok(new OrderResponseDto(1L, 10000, 5000, "2025-04-04 10:00", OrderStatus.COMPLETED));
    }
}
