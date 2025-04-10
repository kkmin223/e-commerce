package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.CouponNotFoundException;
import kr.hhplus.be.server.interfaces.common.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponItemServiceTest {

    @InjectMocks
    private CouponItemService couponItemService;

    @Mock
    private CouponItemRepository couponItemRepository;

    @Test
    void 쿠폰과_사용자를_전달받아_쿠폰_아이템을_발급한다() {
        // given
        TestCoupon coupon = new TestCoupon("쿠폰", 10);
        User user = User.of(1L, 1_000);

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(coupon, user);

        Mockito.when(couponItemRepository.save(Mockito.any(CouponItem.class))).thenReturn(CouponItem.of(user, coupon, Boolean.FALSE));

        // when
        CouponItem couponItem = couponItemService.issueCouponItem(command);

        // then
        assertThat(couponItem)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(user, coupon, Boolean.FALSE);
    }

    @Test
    void 쿠폰_아이템을_발급할_때_사용자가_null인_경우_발급을_실패한다() {
        // given
        TestCoupon coupon = new TestCoupon("쿠폰", 10);
        User user = null;

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(coupon, user);

        // when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> couponItemService.issueCouponItem(command));

        // then
        assertThat(exception)
            .extracting(UserNotFoundException::getCode, UserNotFoundException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUNT.getCode(), ErrorCode.USER_NOT_FOUNT.getMessage());

        verify(couponItemRepository, never()).save(Mockito.any(CouponItem.class));
    }

    @Test
    void 쿠폰_아이템을_발급할_때_쿠폰이_null인_경우_발급을_실패한다() {
        // given
        TestCoupon coupon = null;
        User user = User.of(1L, 1_000);

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(coupon, user);

        // when
        CouponNotFoundException exception = assertThrows(CouponNotFoundException.class, () -> couponItemService.issueCouponItem(command));

        // then
        assertThat(exception)
            .extracting(CouponNotFoundException::getCode, CouponNotFoundException::getMessage)
            .containsExactly(ErrorCode.COUPON_NOT_FOUND.getCode(), ErrorCode.COUPON_NOT_FOUND.getMessage());

        verify(couponItemRepository, never()).save(Mockito.any(CouponItem.class));
    }


}
