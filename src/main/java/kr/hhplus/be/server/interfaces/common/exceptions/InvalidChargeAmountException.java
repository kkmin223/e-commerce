package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidChargeAmountException extends CustomException {
    public InvalidChargeAmountException() {
        super(ErrorCode.INVALID_CHARGE_AMOUNT, HttpStatus.BAD_REQUEST);
    }
}
