package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends CustomException {
    public InsufficientStockException() {
        super(ErrorCode.INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
    }
}
