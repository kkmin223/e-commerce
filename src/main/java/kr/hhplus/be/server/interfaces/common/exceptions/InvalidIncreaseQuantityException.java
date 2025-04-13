package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidIncreaseQuantityException extends CustomException {
    public InvalidIncreaseQuantityException() {
        super(ErrorCode.INVALID_INCREASE_QUANTITY, HttpStatus.BAD_REQUEST);
    }
}
