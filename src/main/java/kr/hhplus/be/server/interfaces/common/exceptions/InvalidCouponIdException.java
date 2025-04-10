package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidCouponIdException extends CustomException {
    public InvalidCouponIdException() {
        super(ErrorCode.INVALID_COUPON_ID, HttpStatus.BAD_REQUEST);
    }
}
