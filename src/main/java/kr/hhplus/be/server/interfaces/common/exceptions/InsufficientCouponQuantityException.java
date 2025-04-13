package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InsufficientCouponQuantityException extends CustomException {
    public InsufficientCouponQuantityException() {
        super(ErrorCode.INSUFFICIENT_COUPON_QUANTITY, HttpStatus.BAD_REQUEST);
    }
}
