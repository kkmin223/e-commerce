package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemCommand;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Facade
public class UserFacade {

    private final UserService userService;
    private final CouponItemService couponItemService;

    public List<UserResult.UserCoupon> listUserCoupons(UserCriteria.UserCouponList criteria) {
        User user = userService.getUser(UserCommand.Get.of(criteria.getUserId()));
        List<CouponItem> userCouponItems = couponItemService.findByUser(CouponItemCommand.FindByUser.of(user));

        return userCouponItems.stream()
            .map(couponItem -> UserResult.UserCoupon.of(couponItem.getId(), couponItem.getCouponName(), couponItem.getIsUsed(), couponItem.getCouponLabel(), couponItem.getCouponType()))
            .toList();

    }

}
