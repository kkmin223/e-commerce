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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Facade
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;
    private final CouponItemService couponItemService;

    @Transactional
    public CouponResult.Issue IssueCoupon(CouponCriteria.Issue criteria) {

        User user = userService.getUser(UserCommand.Get.of(criteria.getUserId()));
        Coupon issuableCoupon = couponService.getIssuableCoupon(CouponCommand.Get.of(criteria.getCouponId()));
        CouponItem couponItem = couponItemService.issueCouponItem(CouponItemCommand.Issue.of(issuableCoupon, user));

        return CouponResult.Issue.of(couponItem.getId(), issuableCoupon.getTitle(), couponItem.getIsUsed(), issuableCoupon.getDiscountLabel(), issuableCoupon.getCouponType());
    }

    public void requestCoupon(CouponCriteria.Request criteria) {
        User user = userService.getUser(UserCommand.Get.of(criteria.getUserId()));
        Coupon issuableCoupon = couponService.getIssuableCoupon(CouponCommand.Get.of(criteria.getCouponId()));

        couponService.sendCouponEvent(CouponCommand.Request.of(issuableCoupon.getId(), user.getId(), criteria.getIssuedAt()));
    }

    @Transactional
    public void processCouponRequest(CouponCriteria.Process criteria) {
        List<Coupon> allIssuableCoupons = couponService.getAllIssuableCoupons();

        for (Coupon coupon : allIssuableCoupons) {
            if (!coupon.canIssue()) {
                continue;
            }

            int issueQuantity = Math.min(criteria.getIssueQuantity(), coupon.getRemainingQuantity());

            Set<String> issueUserIds = couponService.getIssueRequest(CouponCommand.GetIssueRequest.of(coupon.getId(), issueQuantity));
            for (String userId : issueUserIds) {
                if (couponService.isDuplicatedUser(CouponCommand.IsDuplicatedUser.of(coupon.getId(), Long.valueOf(userId)))) {
                    continue;
                }

                try {
                    User user = userService.getUser(UserCommand.Get.of(Long.valueOf(userId)));
                    couponItemService.issueCouponItem(CouponItemCommand.Issue.of(coupon, user));
                    couponService.deleteIssueRequest(CouponCommand.DeleteIssueRequest.of(coupon.getId(), Long.valueOf(userId)));
                } catch (Exception e) {
                    log.error("쿠폰 발급 실패:{}", e.getMessage());
                }
            }
        }
    }
}
