package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidOrderQuantityException extends CustomException {
    public InvalidOrderQuantityException() {
        super(ErrorCode.INVALID_ORDER_QUANTITY, HttpStatus.BAD_REQUEST);
    }
}
