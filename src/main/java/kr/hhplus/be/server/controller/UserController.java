package kr.hhplus.be.server.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.api.UserApi;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.dto.AmountResponseDto;
import kr.hhplus.be.server.dto.ChargeRequestDto;
import kr.hhplus.be.server.dto.CouponResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController implements UserApi {
    @Override
    public ResponseEntity<AmountResponseDto> getUserAmount(long id) {
        return ResponseEntity.ok(new AmountResponseDto(id, 5000));
    }

    @Override
    public ResponseEntity<AmountResponseDto> chargeUserAmount(
        @Min(value = 1, message = "사용자 식별자가 유효하지 않습니다.") long id,
        @Valid ChargeRequestDto chargeRequestDto) {
        return ResponseEntity.ok(new AmountResponseDto(id, 5000));
    }

    @Override
    public ResponseEntity<List<CouponResponseDto>> getUserCoupons(long id) {
        return ResponseEntity.ok(List.of(new CouponResponseDto(1L, "쿠폰1", true, 1000, CouponType.AMOUNT)));
    }
}
