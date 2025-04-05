package kr.hhplus.be.server.interfaces.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {
    ORDER("200", "주문 성공"),
    LIST_PRODUCT("200", "상품 리스트 조회 성공"),
    GET_PRODUCT("200", "상품 조회 성공"),
    LIST_POPULAR_PRODUCT("200", "인기 상품 조회 성공"),
    GET_USER_AMOUNT("200", "잔액 조회 성공"),
    CHARGE_USER_AMOUNT("200", "잔액 충전 성공"),
    LIST_USER_COUPONS("200", "사용자 보유 쿠폰 조회 성공"),
    ISSUE_COUPON("200", "선착순 쿠폰 발급 성공");
    private final String code;
    private final String message;
}
