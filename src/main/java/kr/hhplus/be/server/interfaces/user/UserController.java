package kr.hhplus.be.server.interfaces.user;

import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
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

    private final UserService userService;

    @Override
    @GetMapping("/{id}/amount")
    public ResponseEntity<ApiResult<UserResponse.UserAmount>> getUserAmount(long id) {
        User user = userService.getUser(UserCommand.Get.of(id));
        return ResponseEntity.ok(
            ApiResult.of(
                SuccessCode.CHARGE_USER_AMOUNT,
                new UserResponse.UserAmount(user.getId(), user.getAmount())));
    }

    @Override
    @PostMapping("/{id}/amount/charge")
    public ResponseEntity<ApiResult<UserResponse.UserAmount>> chargeUserAmount(long id, UserRequest.Charge chargeRequest) {
        User user = userService.charge(chargeRequest.toCommand(id));
        return ResponseEntity.ok(ApiResult.of(SuccessCode.CHARGE_USER_AMOUNT, new UserResponse.UserAmount(user.getId(), user.getAmount())));
    }

    @Override
    @GetMapping("/{id}/coupons")
    public ResponseEntity<ApiResult<UserResponse.UserCoupon>> getUserCoupons(long id) {
        return ResponseEntity.ok(
            ApiResult.of(SuccessCode.LIST_USER_COUPONS, new UserResponse.UserCoupon(id, List.of(new CouponResponse.Coupon(1L, "쿠폰1", true, 1000, CouponType.AMOUNT))))
        );
    }
}
