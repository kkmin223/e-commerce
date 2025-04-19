package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateOrder {
        private User orderUser;
        private Map<Product, Integer> productQuantities;
        private LocalDateTime orderAt;

        public static CreateOrder of(User user, Map<Product, Integer> productQuantities, LocalDateTime orderAt) {
            return new CreateOrder(user, productQuantities, orderAt);
        }
    }


}
