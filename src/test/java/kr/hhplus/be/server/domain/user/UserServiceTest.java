package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidChargeAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void 잔액을_충전한다() {
        // given
        Long userId = 1L;
        Integer initialAmount = 100;
        Integer chargeAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        Mockito.when(userRepository.getUser(userId)).thenReturn(User.of(userId, initialAmount));

        // when
        User user = userService.charge(chargeCommand);

        // then
        assertThat(user)
            .extracting(User::getId, User::getAmount)
            .containsExactly(userId, initialAmount + chargeAmount);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    void 충전_금액이_0보다_작거나_같을때_충전이_실패한다(Integer chargeAmount) {
        //given
        Long userId = 1L;
        Integer initialAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        Mockito.when(userRepository.getUser(userId)).thenReturn(User.of(userId, initialAmount));
        //when
        InvalidChargeAmountException exception = assertThrows(InvalidChargeAmountException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(InvalidChargeAmountException::getCode, InvalidChargeAmountException::getMessage)
            .containsExactly(ErrorCode.INVALID_CHARGE_AMOUNT.getCode(), ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }

}
