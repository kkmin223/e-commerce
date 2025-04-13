package kr.hhplus.be.server.interfaces.common.exceptions;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import org.springframework.http.HttpStatus;

public class BelowMinimumBalancePolicyException extends CustomException {
    public BelowMinimumBalancePolicyException() {
        super(ErrorCode.BELOW_MINIMUM_BALANCE_POLICY, HttpStatus.BAD_REQUEST);
    }
}
