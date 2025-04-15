package kr.hhplus.be.server.application.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserCouponList {
        private Long userId;

        public static UserCouponList of (Long userId) {
            return new UserCouponList(userId);
        }
    }
}
