package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class OrderProductNotFoundException extends CustomException {
    public OrderProductNotFoundException() {
        super(ErrorCode.ORDER_PRODUCT_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
}
