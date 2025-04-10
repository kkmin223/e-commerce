package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidProductIdException extends CustomException {
    public InvalidProductIdException() {
        super(ErrorCode.INVALID_PRODUCT_ID, HttpStatus.BAD_REQUEST);
    }
}
