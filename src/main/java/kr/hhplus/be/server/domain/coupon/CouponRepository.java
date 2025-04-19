package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> getCoupon(Long id);
    Coupon save(Coupon coupon);
}
