package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class CouponAlreadyUsedException extends CustomException {
    public CouponAlreadyUsedException() {
        super(ErrorCode.COUPON_ALREADY_USED, HttpStatus.BAD_REQUEST);
    }
}
