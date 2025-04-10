package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemCommand;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponFacadeTest {

    @InjectMocks
    private CouponFacade couponFacade;

    @Mock
    private CouponService couponService;
    @Mock
    private CouponItemService couponItemService;
    @Mock
    private UserService userService;

    @Test
    void 선착순_쿠폰을_발급한다() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(userId, couponId);

        User user = User.of(userId, 1_000);
        when(userService.getUser(UserCommand.Get.of(userId))).thenReturn(user);

        TestCoupon coupon = new TestCoupon("쿠폰", 10);
        when(couponService.getIssuableCoupon(CouponCommand.Get.of(couponId))).thenReturn(coupon);

        CouponItem couponItem = CouponItem.of(user, coupon, Boolean.FALSE);
        when(couponItemService.issueCouponItem(CouponItemCommand.Issue.of(coupon, user))).thenReturn(couponItem);

        // when
        CouponResult.Issue result = couponFacade.IssueCoupon(criteria);

        // then
        Assertions.assertThat(result)
            .extracting(CouponResult.Issue::getCouponName, CouponResult.Issue::getIsUsed, CouponResult.Issue::getDiscountLabel, CouponResult.Issue::getCouponType)
            .containsExactly(coupon.getTitle(), Boolean.FALSE, coupon.getDiscountLabel(), coupon.getCouponType());

        InOrder inOrder = Mockito.inOrder(userService, couponService, couponItemService);
        inOrder.verify(userService).getUser(any());
        inOrder.verify(couponService).getIssuableCoupon(any());
        inOrder.verify(couponItemService).issueCouponItem(any());

    }

}
