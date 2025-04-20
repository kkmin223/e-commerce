package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface CouponItemRepository {
    Optional<CouponItem> findById(Long id);

    CouponItem save(CouponItem couponItem);

    List<CouponItem> findByUser(User user);

    int countByCouponId(Long id);
}
