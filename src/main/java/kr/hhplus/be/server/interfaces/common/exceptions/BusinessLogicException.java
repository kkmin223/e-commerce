package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class BusinessLogicException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus status;

    public BusinessLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
