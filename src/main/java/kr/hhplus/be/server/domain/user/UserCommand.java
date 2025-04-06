package kr.hhplus.be.server.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charge {
        private Long userId;
        private Integer chargeAmount;
    }
}
