package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidDeductAmountException extends CustomException {
    public InvalidDeductAmountException() {
        super(ErrorCode.INVALID_DEDUCT_AMOUNT, HttpStatus.BAD_REQUEST);
    }
}
