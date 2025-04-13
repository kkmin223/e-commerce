package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends CustomException {
    public InsufficientBalanceException() {
        super(ErrorCode.INSUFFICIENT_BALANCE, HttpStatus.BAD_REQUEST);
    }
}
