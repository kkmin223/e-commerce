package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    void 유저로_쿠폰_아이템을_조회한다() {
        // given
        User savedUser = userRepository.save(User.of( 1_000));
        CouponItemCommand.FindByUser command = CouponItemCommand.FindByUser.of(savedUser);
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", 10));

        CouponItem couponItem1 = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));
        CouponItem couponItem2 = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));

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

}
