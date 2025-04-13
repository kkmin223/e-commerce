package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class InvalidUserIdException extends CustomException {
    public InvalidUserIdException(){
        super(ErrorCode.INVALID_USER_ID, HttpStatus.BAD_REQUEST);
    }
}
