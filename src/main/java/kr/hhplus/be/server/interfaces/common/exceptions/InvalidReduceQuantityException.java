package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidReduceQuantityException extends CustomException {
    public InvalidReduceQuantityException() {
        super(ErrorCode.INVALID_REDUCE_QUANTITY, HttpStatus.BAD_REQUEST);
    }
}
