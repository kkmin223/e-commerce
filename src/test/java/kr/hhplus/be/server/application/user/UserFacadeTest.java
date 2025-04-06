package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    @Test
    void 유저_잔액을_충전후_유저의_잔액_정보를_반환한다() {
        // given
        Long userId = 1L;
        Integer chargeAmount = 100;
        UserCriteria.Charge chargeCriteria = new UserCriteria.Charge(userId, chargeAmount);

        when(userService.charge(any(UserCommand.Charge.class)))
            .thenReturn(User.of(userId, chargeAmount));

        // when
        UserResult.UserAmount userAmount = userFacade.charge(chargeCriteria);

        // then
        assertThat(userAmount)
            .extracting(UserResult.UserAmount::getUserId, UserResult.UserAmount::getAmount)
            .containsExactly(userId, chargeAmount);

    }
}
