package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.issueCouponItem(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());

        verify(couponItemRepository, never()).save(Mockito.any(CouponItem.class));
    }

    @Test
    void 쿠폰_아이템을_발급할_때_쿠폰이_null인_경우_발급을_실패한다() {
        // given
        TestCoupon coupon = null;
        User user = User.of(1L, 1_000);

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(coupon, user);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.issueCouponItem(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.COUPON_NOT_FOUND.getCode(), ErrorCode.COUPON_NOT_FOUND.getMessage());

        verify(couponItemRepository, never()).save(Mockito.any(CouponItem.class));
    }

    @Test
    void 쿠폰_아이템_식별자로_쿠폰_아이템을_조회한다() {
        // given
        CouponItemCommand.Get command = CouponItemCommand.Get.of(1L);
        TestCoupon coupon = new TestCoupon("쿠폰", 10);
        User user = User.of(1L, 1_000);
        CouponItem couponItem = CouponItem.of(user, coupon, Boolean.FALSE);


        Mockito.when(couponItemRepository.findById(command.getCouponItemId())).thenReturn(Optional.of(couponItem));

        // when
        CouponItem result = couponItemService.getCouponItem(command);

        // then
        assertThat(result)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(user, coupon, Boolean.FALSE);
    }

    @Test
    void 쿠폰_아이템_식별자에_해당하는_쿠폰_아이템이_없는_경우_COUPON_NOT_FOUND_예외가_발생한다() {
        // given
        CouponItemCommand.Get command = CouponItemCommand.Get.of(1L);


        Mockito.when(couponItemRepository.findById(command.getCouponItemId())).thenReturn(Optional.empty());

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.getCouponItem(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.COUPON_NOT_FOUND.getCode(), ErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @Test
    void 유저로_쿠폰_아이템을_조회한다() {
        // given
        User user = User.of(1L, 1_000);
        CouponItemCommand.FindByUser command = CouponItemCommand.FindByUser.of(user);
        TestCoupon coupon = new TestCoupon("쿠폰", 10);

        CouponItem couponItem1 = CouponItem.of(user, coupon, Boolean.FALSE);
        CouponItem couponItem2 = CouponItem.of(user, coupon, Boolean.FALSE);

        Mockito.when(couponItemRepository.findByUser(user)).thenReturn(List.of(couponItem1, couponItem2));

        // when
        List<CouponItem> userCouponItems = couponItemService.findByUser(command);

        // then
        assertThat(userCouponItems)
            .hasSize(2)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactlyInAnyOrder(
                Tuple.tuple(couponItem1.getUser(), couponItem1.getCoupon(), couponItem1.getIsUsed()),
                Tuple.tuple(couponItem2.getUser(), couponItem2.getCoupon(), couponItem2.getIsUsed())
            );
    }

    @Test
    void 유저로_쿠폰_아이템을_조회할_떄_유저가_null이면_USER_NOT_FOUND_예외를_반환한다() {
        // given
        User user = null;
        CouponItemCommand.FindByUser command = CouponItemCommand.FindByUser.of(user);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.findByUser(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
