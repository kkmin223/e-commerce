package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.*;

import java.time.LocalDateTime;

public class CouponCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Get {
        private Long couponId;

        public static Get of(Long couponId) {
            return new Get(couponId);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    public static class Request {
        private Long couponId;
        private Long userId;
        private LocalDateTime issuedAt;

        public static Request of(Long couponId, Long userId, LocalDateTime issuedAt) {
            if (couponId == null
                || couponId <= 0) {
                throw new BusinessLogicException(ErrorCode.INVALID_COUPON_ID);
            }

            if (userId == null
                || userId <= 0) {
                throw new BusinessLogicException(ErrorCode.INVALID_USER_ID);
            }

            return new Request(couponId, userId, issuedAt);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetIssueRequest {
        private Long couponId;
        private Integer issueQuantity;

        public static GetIssueRequest of(Long couponId, Integer issueQuantity) {
            if (couponId == null
                || couponId <= 0) {
                throw new BusinessLogicException(ErrorCode.INVALID_COUPON_ID);
            }

            return new GetIssueRequest(couponId, issueQuantity);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DeleteIssueRequest {
        private Long couponId;
        private Long userId;

        public static DeleteIssueRequest of(Long couponId, Long userId) {
            return new DeleteIssueRequest(couponId, userId);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IsDuplicatedUser {
        private Long couponId;
        private Long userId;

        public static IsDuplicatedUser of(Long couponId, Long userId) {
            return new IsDuplicatedUser(couponId, userId);
        }
    }
}
