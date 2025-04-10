package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/coupons")
public class CouponController implements CouponApi {
    @Override
    @PostMapping("/{id}/issue")
    public ResponseEntity<ApiResult<CouponResponse.Coupon>> issueCoupon(long id, CouponRequest.Issue issueRequest) {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.ISSUE_COUPON, new CouponResponse.Coupon(1L, "쿠폰1", true, 1000, CouponType.AMOUNT)));
    }
}
