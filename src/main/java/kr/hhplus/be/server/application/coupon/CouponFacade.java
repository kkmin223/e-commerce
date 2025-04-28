package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemCommand;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.lock.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Facade
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;
    private final CouponItemService couponItemService;

    @DistributedLock(
        keys = "T(kr.hhplus.be.server.lock.LockKeyGenerator).generateForIssueCoupon(#criteria)"
    )
    @Transactional
    public CouponResult.Issue IssueCoupon(CouponCriteria.Issue criteria) {

        User user = userService.getUser(UserCommand.Get.of(criteria.getUserId()));
        Coupon issuableCoupon = couponService.getIssuableCoupon(CouponCommand.Get.of(criteria.getCouponId()));
        CouponItem couponItem = couponItemService.issueCouponItem(CouponItemCommand.Issue.of(issuableCoupon, user));

        return CouponResult.Issue.of(couponItem.getId(), issuableCoupon.getTitle(), couponItem.getIsUsed(), issuableCoupon.getDiscountLabel(), issuableCoupon.getCouponType());
    }
}
