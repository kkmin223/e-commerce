package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.user.UserCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Charge {
        private Long userId;
        private Integer chargeAmount;

        public UserCommand.Charge toCommand() {
            return new UserCommand.Charge(userId, chargeAmount);
        }
    }
}
