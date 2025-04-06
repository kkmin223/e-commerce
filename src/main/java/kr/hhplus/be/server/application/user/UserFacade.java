package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;

@Facade
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserResult.UserAmount charge(UserCriteria.Charge criteria) {
        User user = userService.charge(criteria.toCommand());

        return new UserResult.UserAmount(user.getId(), user.getAmount());
    }
}

