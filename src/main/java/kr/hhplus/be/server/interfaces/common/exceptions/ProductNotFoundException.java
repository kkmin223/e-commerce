package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends CustomException {
    public ProductNotFoundException() {
        super(ErrorCode.PRODUCT_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }
}
