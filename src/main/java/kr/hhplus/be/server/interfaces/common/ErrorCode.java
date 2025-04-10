package kr.hhplus.be.server.interfaces.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements Code {
    INVALID_CHARGE_AMOUNT("INVALID_CHARGE_AMOUNT", "충전 금액이 유효하지 않습니다."),
    INVALID_DEDUCT_AMOUNT("INVALID_DEDUCT_AMOUNT", "차감 금액이 유효하지 않습니다."),
    BELOW_MINIMUM_BALANCE_POLICY("BELOW_MINIMUM_BALANCE_POLICY", "금액이 최소 정책 금액보다 작을 수 없습니다."),
    INVALID_REDUCE_QUANTITY("INVALID_REDUCE_QUANTITY", "차감 수량이 유효하지 않습니다."),
    INVALID_INCREASE_QUANTITY("INVALID_INCREASE_QUANTITY", "복구 수량이 유효하지 않습니다."),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "재고가 부족합니다."),
    INVALID_PRODUCT_ID("INVALID_PRODUCT_ID", "상품 식별자가 유효하지 않습니다."),
    ORDER_PRODUCT_NOT_FOUND("ORDER_PRODUCT_NOT_FOUND", "요청한 상품 중 존재하지 않는 상품이 있습니다."),
    COUPON_ALREADY_USED("COUPON_ALREADY_USED", "이미 사용한 쿠폰입니다."),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "잔액이 부족합니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자가 존재하지 않습니다."),
    INVALID_USER_ID("INVALID_USER_ID", "사용자 식별자가 유효하지 않습니다."),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품이 존재하지 않습니다."),
    INVALID_ORDER_QUANTITY("INVALID_ORDER_QUANTITY", "주문 수량이 유효하지 않습니다."),
    INSUFFICIENT_COUPON_QUANTITY("INSUFFICIENT_COUPON_QUANTITY", "쿠폰 수량이 부족합니다."),
    COUPON_NOT_FOUND("COUPON_NOT_FOUND", "쿠폰이 존재하지 않습니다."),
    INVALID_COUPON_ID("INVALID_COUPON_ID", "쿠폰 식별자가 유효하지 않습니다."),
    ;

    private final String code;
    private final String message;
}
