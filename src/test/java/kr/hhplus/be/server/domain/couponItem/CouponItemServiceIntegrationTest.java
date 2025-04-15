package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class CouponItemServiceIntegrationTest {

    @Autowired
    private CouponItemService couponItemService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 쿠폰과_사용자를_전달받아_쿠폰_아이템을_발급한다() {
        // given
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", 10));
        User savedUser = userRepository.save(User.of(1_000));

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(savedCoupon, savedUser);

        // when
        CouponItem couponItem = couponItemService.issueCouponItem(command);

        // then
        assertThat(couponItem)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(savedUser, savedCoupon, Boolean.FALSE);
    }

    @Test
    void 쿠폰_아이템을_발급할_때_사용자가_null인_경우_USER_NOT_FOUND_예외가_발생한다() {
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
    }

    @Test
    void 쿠폰_아이템을_발급할_때_쿠폰이_null인_경우_COUPON_NOT_FOUND_예외가_발생한다() {
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
    }

    @Test
    void 쿠폰_수량이_0인_쿠폰으로_쿠폰_아이템을_발급하면_INSUFFICIENT_COUPON_QUANTITY_예외가_발생한다() {
        // given
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", 0));
        User savedUser = userRepository.save(User.of(1_000));

        CouponItemCommand.Issue command = CouponItemCommand.Issue.of(savedCoupon, savedUser);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.issueCouponItem(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getCode(), ErrorCode.INSUFFICIENT_COUPON_QUANTITY.getMessage());
    }

    @Test
    void 쿠폰_아이템_식별자로_쿠폰_아이템을_조회한다() {
        // given
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", 10));
        User savedUser = userRepository.save(User.of(1_000));
        CouponItem savedCouponItem = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));
        CouponItemCommand.Get command = CouponItemCommand.Get.of(savedCouponItem.getId());


        // when
        CouponItem result = couponItemService.getCouponItem(command);

        // then
        assertThat(result)
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(savedCouponItem.getUser(), savedCouponItem.getCoupon(), savedCouponItem.getIsUsed());
    }

    @Test
    void 쿠폰_아이템_식별자에_해당하는_쿠폰_아이템이_없는_경우_COUPON_NOT_FOUND_예외가_발생한다() {
        // given
        CouponItemCommand.Get command = CouponItemCommand.Get.of(1L);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> couponItemService.getCouponItem(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.COUPON_NOT_FOUND.getCode(), ErrorCode.COUPON_NOT_FOUND.getMessage());
    }


}
