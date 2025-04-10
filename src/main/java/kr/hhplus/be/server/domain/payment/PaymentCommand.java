package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateAndProcess {
        private User user;
        private Order order;
        private CouponItem couponItem;

        public static CreateAndProcess of(User user, Order order, CouponItem couponItem) {
            return new CreateAndProcess(user, order, couponItem);
        }
    }
}
