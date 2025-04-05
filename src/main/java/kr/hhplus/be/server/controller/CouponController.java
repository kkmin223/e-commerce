package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.api.CouponApi;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.dto.CouponIssueRequestDto;
import kr.hhplus.be.server.dto.CouponResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class CouponController implements CouponApi {
    @Override
    public ResponseEntity<CouponResponseDto> issueCoupon(long id, CouponIssueRequestDto couponIssueRequestDto) {
        return ResponseEntity.ok(new CouponResponseDto(1L, "쿠폰1", true, 1000, CouponType.AMOUNT));
    }
}
