package kr.hhplus.be.server.infrastructure.couponItem;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CouponItemRepositoryImpl implements CouponItemRepository {
    @Override
    public CouponItem getCouponItem(Long id) {
        return null;
    }

    @Override
    public CouponItem save(CouponItem couponItem) {
        return null;
    }
}
