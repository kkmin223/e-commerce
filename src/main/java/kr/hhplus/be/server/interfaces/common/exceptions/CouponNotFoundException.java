package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class CouponNotFoundException extends CustomException {
    public CouponNotFoundException() {
        super(ErrorCode.COUPON_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
}
