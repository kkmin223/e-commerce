package kr.hhplus.be.server.application.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResult {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAmount {
        private Long userId;
        private Integer amount;
    }
}
