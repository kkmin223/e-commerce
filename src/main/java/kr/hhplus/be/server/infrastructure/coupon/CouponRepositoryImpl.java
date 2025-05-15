package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findByIdForUpdate(Long id) {
        return couponJpaRepository.findByIdForUpdate(id);
    }

    @Override
    public void requestCoupon(Long couponId, Long userId, LocalDateTime issuedAt) {
        couponRedisRepository.requestCoupon(couponId, userId, issuedAt);
    }

    @Override
    public Set<String> getIssueRequest(Long couponId, Integer issueQuantity) {
        return couponRedisRepository.getIssueRequest(couponId, issueQuantity);
    }

    @Override
    public List<Coupon> getAllIssuableCoupons() {
        return couponJpaRepository.findCouponsByRemainingQuantityGreaterThan(0);
    }

    @Override
    public void deleteIssueRequest(Long couponId, Long userId) {
        couponRedisRepository.deleteIssueRequest(couponId, userId);
    }

    @Override
    public boolean isDuplicatedUser(Long couponId, Long userId) {
        Long result = couponRedisRepository.addIssuedUser(couponId, userId);
        return result == null || result == 0;
    }
}
