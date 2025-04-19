package kr.hhplus.be.server.application.coupon;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CouponFacadeIntegrationTest {

    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Test
    void 선착순_쿠폰을_발급한다() {
        // given
        User savedUser = userRepository.save(User.of(1_000));
        Integer initialQuantity = 10;
        Coupon savedCoupon = couponRepository.save(new TestCoupon("쿠폰", initialQuantity));

        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(savedUser.getId(), savedCoupon.getId());

        // when
        CouponResult.Issue result = couponFacade.IssueCoupon(criteria);

        // then
        // 결과 검증
        assertThat(result)
            .extracting(CouponResult.Issue::getCouponName, CouponResult.Issue::getIsUsed, CouponResult.Issue::getDiscountLabel, CouponResult.Issue::getCouponType)
            .containsExactly(savedCoupon.getTitle(), Boolean.FALSE, savedCoupon.getDiscountLabel(), savedCoupon.getCouponType());

        // 쿠폰 검증
        assertThat(savedCoupon.getRemainingQuantity())
            .isEqualTo(initialQuantity - 1);

        // 쿠폰 아이템 검증
        Optional<CouponItem> expectedCouponItem = couponItemRepository.getCouponItem(result.getId());

        assertThat(expectedCouponItem)
            .isNotNull();

        assertThat(expectedCouponItem.get())
            .extracting(CouponItem::getUser, CouponItem::getCoupon, CouponItem::getIsUsed)
            .containsExactly(savedUser, savedCoupon, Boolean.FALSE);
    }


}
