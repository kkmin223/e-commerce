package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController implements UserApi {
    @Override
    @GetMapping("/{id}/amount")
    public ResponseEntity<ApiResult<UserResponse.UserAmount>> getUserAmount(long id) {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.CHARGE_USER_AMOUNT, new UserResponse.UserAmount(id, 5000)));
    }

    @Override
    @PostMapping("/{id}/amount/charge")
    public ResponseEntity<ApiResult<UserResponse.UserAmount>> chargeUserAmount(long id, UserRequest.Charge chargeRequestDto) {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.CHARGE_USER_AMOUNT, new UserResponse.UserAmount(id, 5000)));
    }

    @Override
    @GetMapping("/{id}/coupons")
    public ResponseEntity<ApiResult<UserResponse.UserCoupon>> getUserCoupons(long id) {
        return ResponseEntity.ok(
            ApiResult.of(SuccessCode.LIST_USER_COUPONS, new UserResponse.UserCoupon(id, List.of(new CouponResponse.Coupon(1L, "쿠폰1", true, 1000, CouponType.AMOUNT))))
        );
    }
}
