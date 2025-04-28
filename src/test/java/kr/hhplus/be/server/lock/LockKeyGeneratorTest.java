package kr.hhplus.be.server.lock;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.user.UserCommand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class LockKeyGeneratorTest {

    private static final String USER_LOCK_PREFIX = "USER";
    private static final String PRODUCT_LOCK_PREFIX = "PRODUCT";
    private static final String COUPON_LOCK_PREFIX = "COUPON";
    private static final String COUPON_ITEM_LOCK_PREFIX = "COUPON_ITEM";

    @Test
    void 주문_결재를_위한_사용자_상품_쿠폰아이템_LockKey를_생성한다() {
        // given
        Long userId = 1L;
        Long couponItemId = 1L;
        Long productId = 1L;
        OrderCriteria.OrderProduct orderProduct = OrderCriteria.OrderProduct.of(productId, 1);
        OrderCriteria.OrderAndPay criteria = OrderCriteria.OrderAndPay.of(1L, 1L, List.of(orderProduct), LocalDateTime.now());

        // when
        String[] lockKeys = LockKeyGenerator.generateForOrderAndPay(criteria);

        // then
        Assertions.assertThat(lockKeys)
            .hasSize(3)
            .containsExactlyInAnyOrder(
                formatKey(USER_LOCK_PREFIX, userId),
                formatKey(PRODUCT_LOCK_PREFIX, productId),
                formatKey(COUPON_ITEM_LOCK_PREFIX, couponItemId)
            );
    }

    @Test
    void 주문_결재를_위한_사용자_상품_LockKey를_생성한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        OrderCriteria.OrderProduct orderProduct = OrderCriteria.OrderProduct.of(productId, 1);
        OrderCriteria.OrderAndPay criteria = OrderCriteria.OrderAndPay.of(1L, null, List.of(orderProduct), LocalDateTime.now());

        // when
        String[] lockKeys = LockKeyGenerator.generateForOrderAndPay(criteria);

        // then
        Assertions.assertThat(lockKeys)
            .hasSize(2)
            .containsExactlyInAnyOrder(
                formatKey(USER_LOCK_PREFIX, userId),
                formatKey(PRODUCT_LOCK_PREFIX, productId)
            );
    }

    @Test
    void 충전을_위한_사용자_LockKey를_생성한다() {
        // given
        Long userId = 1L;
        UserCommand.Charge command = UserCommand.Charge.of(userId, 500);

        // when
        String[] lockKeys = LockKeyGenerator.generateForCharge(command);

        // then
        Assertions.assertThat(lockKeys)
            .hasSize(1)
            .contains(formatKey(USER_LOCK_PREFIX, userId));
    }

    @Test
    void 쿠폰_발급을_위한_쿠폰_LockKey를_생성한다() {
        // given
        Long couponId = 1L;
        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(1L, couponId);

        // when
        String[] lockKeys = LockKeyGenerator.generateForIssueCoupon(criteria);


        // then
        Assertions.assertThat(lockKeys)
            .hasSize(1)
            .contains(formatKey(COUPON_LOCK_PREFIX, couponId));
    }

    private String formatKey(String prefix, Long id) {
        return String.format("%s:%d", prefix, id);
    }
}
