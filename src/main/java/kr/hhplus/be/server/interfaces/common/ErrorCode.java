package kr.hhplus.be.server.interfaces.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements Code {
    INVALID_CHARGE_AMOUNT("INVALID_CHARGE_AMOUNT", "충전 금액이 유효하지 않습니다."),
    INVALID_DEDUCT_AMOUNT("INVALID_DEDUCT_AMOUNT", "차감 금액이 유효하지 않습니다."),
    BELOW_MINIMUM_BALANCE_POLICY("BELOW_MINIMUM_BALANCE_POLICY", "금액이 최소 정책 금액보다 작을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
