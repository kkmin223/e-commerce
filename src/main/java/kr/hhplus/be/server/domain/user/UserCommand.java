package kr.hhplus.be.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Charge {
        private Long userId;
        private Integer chargeAmount;

        public static Charge of(Long userId, Integer chargeAmount) {
            return new Charge(userId, chargeAmount);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Get {
        private Long userId;

        public static Get of(Long userId) {
            return new Get(userId);
        }

    }
}
