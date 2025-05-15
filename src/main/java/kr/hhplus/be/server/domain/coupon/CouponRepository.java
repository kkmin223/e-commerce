package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CouponRepository {
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);
    Optional<Coupon> findByIdForUpdate(Long id);
    void requestCoupon(Long couponId, Long userId, LocalDateTime issuedAt);
    Set<String> getIssueRequest(Long couponId, Integer issueQuantity);
    List<Coupon> getAllIssuableCoupons();
    void deleteIssueRequest(Long couponId, Long userId);
    boolean isDuplicatedUser(Long couponId, Long userId);
}
