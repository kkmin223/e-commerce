package kr.hhplus.be.server.lock;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.user.UserCommand;

import java.util.ArrayList;
import java.util.List;

public class LockKeyGenerator {

    private static final String USER_LOCK_PREFIX = "USER";
    private static final String PRODUCT_LOCK_PREFIX = "PRODUCT";
    private static final String COUPON_LOCK_PREFIX = "COUPON";
    private static final String COUPON_ITEM_LOCK_PREFIX = "COUPON_ITEM";

    /**
     * 주문/결제 LockKey 생성
     *
     * @param criteria
     * @return
     */
    public static String[] generateForOrderAndPay(OrderCriteria.OrderAndPay criteria) {
        List<String> keys = new ArrayList<>();

        if (criteria.getUserId() != null) {
            keys.add(formatKey(USER_LOCK_PREFIX, criteria.getUserId()));
        }

        if (criteria.getOrderProducts() != null
            && !criteria.getOrderProducts().isEmpty()) {
            List<Long> productIds = criteria.getOrderProducts().stream()
                .map(OrderCriteria.OrderProduct::getProductId)
                .distinct()
                .sorted()
                .toList();

            for (Long productId : productIds) {
                keys.add(formatKey(PRODUCT_LOCK_PREFIX, productId));
            }
        }

        boolean hasCoupon = criteria.getCouponItemId() != null
            && 0 < criteria.getCouponItemId();
        if (hasCoupon) {
            keys.add(formatKey(COUPON_ITEM_LOCK_PREFIX, criteria.getCouponItemId()));
        }

        return keys.toArray(new String[0]);
    }

    /**
     * 충전 LockKey 생성
     *
     * @param command
     * @return
     */
    public static String[] generateForCharge(UserCommand.Charge command) {
        return new String[]{formatKey(USER_LOCK_PREFIX, command.getUserId())};
    }

    /**
     * 쿠폰 발급 LockKey 생성
     *
     * @param criteria
     * @return
     */
    public static String[] generateForIssueCoupon(CouponCriteria.Issue criteria) {
        return new String[]{formatKey(COUPON_LOCK_PREFIX, criteria.getCouponId())};
    }

    private static String formatKey(String prefix, Long id) {
        return String.format("%s:%d", prefix, id);
    }

}
