package kr.hhplus.be.server.application.coupon;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CouponCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Issue {
        private Long userId;
        private Long couponId;

        public static Issue of(Long userId, Long couponId) {
            return new Issue(userId, couponId);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        private Long userId;
        private Long couponId;
        private LocalDateTime issuedAt;

        public static Request of(Long userId, Long couponId, LocalDateTime issuedAt) {
            return new Request(userId, couponId, issuedAt);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Process {
        private Integer issueQuantity;

        public static Process of(Integer issueQuantity) {
            return new Process(issueQuantity);
        }
    }
}
