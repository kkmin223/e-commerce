package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.TestCoupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
@SpringBootTest
public class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void 유저가_보유한_쿠폰_아이템_리스트를_조회한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000));
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", 10));
        CouponItem savedCouponItem1 = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));
        CouponItem savedCouponItem2 = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, Boolean.FALSE));

        UserCriteria.UserCouponList criteria = UserCriteria.UserCouponList.of(savedUser.getId());

        // when
        List<UserResult.UserCoupon> userCoupons = userFacade.listUserCoupons(criteria);

        // then
        assertThat(userCoupons)
            .hasSize(2)
            .extracting(UserResult.UserCoupon::getId, UserResult.UserCoupon::getCouponName, UserResult.UserCoupon::getIsUsed, UserResult.UserCoupon::getDiscountLabel, UserResult.UserCoupon::getCouponType)
            .containsExactlyInAnyOrder(
                tuple(savedCouponItem1.getId(), savedCouponItem1.getCouponName(), savedCouponItem1.getIsUsed(), savedCouponItem1.getCouponLabel(), savedCouponItem1.getCouponType()),
                tuple(savedCouponItem2.getId(), savedCouponItem2.getCouponName(), savedCouponItem2.getIsUsed(), savedCouponItem2.getCouponLabel(), savedCouponItem2.getCouponType())
            );

    }


}
