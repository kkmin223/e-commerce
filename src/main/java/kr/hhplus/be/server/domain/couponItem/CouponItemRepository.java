package kr.hhplus.be.server.domain.couponItem;

import java.util.Optional;

public interface CouponItemRepository {
    Optional<CouponItem> getCouponItem(Long id);

    CouponItem save(CouponItem couponItem);
}
