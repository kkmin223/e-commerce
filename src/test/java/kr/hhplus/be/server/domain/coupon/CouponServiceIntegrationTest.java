package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void 쿠폰_식별자를_입력받아_발급할_수_있는_쿠폰이면_반환한다() {
        // given
        TestCoupon testCoupon = new TestCoupon("쿠폰", 10);
        Coupon savedCoupon = couponRepository.save(testCoupon);
        CouponCommand.Get command = CouponCommand.Get.of(savedCoupon.getId());

        couponRepository.save(testCoupon);

        // when
        Coupon result = couponService.getIssuableCoupon(command);

        // then
        assertThat(result)
            .extracting(Coupon::getId, Coupon::getTitle, Coupon::getInitialQuantity, Coupon::getRemainingQuantity)
            .containsExactly(testCoupon.getId(), testCoupon.getTitle(), testCoupon.getInitialQuantity(), testCoupon.getRemainingQuantity());
    }
}
