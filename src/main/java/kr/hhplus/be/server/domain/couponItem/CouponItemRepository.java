package kr.hhplus.be.server.domain.couponItem;

public interface CouponItemRepository {
    CouponItem getCouponItem(Long id);
    CouponItem save(CouponItem couponItem);
}
