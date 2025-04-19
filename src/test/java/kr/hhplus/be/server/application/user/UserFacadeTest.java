package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    @Mock
    private CouponItemService couponItemService;

    @Test
    void 유저가_보유한_쿠폰_아이템_리스트를_조회한다() {
        // given
        Long userId = 1L;

        User user = User.of(userId, 1_000);
        TestCoupon testCoupon = new TestCoupon("쿠폰", 10);
        CouponItem couponItem = CouponItem.of(user, testCoupon, Boolean.FALSE);

        Mockito.when(userService.getUser(any())).thenReturn(user);
        Mockito.when(couponItemService.findByUser(any())).thenReturn(List.of(couponItem));
        UserCriteria.UserCouponList criteria = UserCriteria.UserCouponList.of(userId);

        // when
        List<UserResult.UserCoupon> userCoupons = userFacade.listUserCoupons(criteria);

        // then
        InOrder inOrder = Mockito.inOrder(userService, couponItemService);
        inOrder.verify(userService).getUser(any());
        inOrder.verify(couponItemService).findByUser(any());

    }
}
