package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface CouponItemRepository {
    Optional<CouponItem> getCouponItem(Long id);

    CouponItem save(CouponItem couponItem);

    List<CouponItem> findByUser(User user);
}
