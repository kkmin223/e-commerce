package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.of(userId, initialAmount)));

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

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.of(userId, initialAmount)));
        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_CHARGE_AMOUNT.getCode(), ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }

    @Test
    void 잔액을_충전할때_유저_식별자가_null이면_충전에_실패한다() {
        //given
        Long userId = null;
        Integer initialAmount = 100;
        Integer chargeAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_USER_ID.getCode(), ErrorCode.INVALID_USER_ID.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void 잔액을_충전할때_유저_식별자가_0보다_작거나_같으면_충전에_실패한다(Long userId) {
        //given
        Integer initialAmount = 100;
        Integer chargeAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_USER_ID.getCode(), ErrorCode.INVALID_USER_ID.getMessage());
    }

    @Test
    void 잔액을_충전할때_사용자가_없으면_충전에_실패한다() {
        //given
        Long userId = 1L;
        Integer initialAmount = 100;
        Integer chargeAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 잔액을_충전할때_충전금액이_null이면_충전에_실패한다() {
        //given
        Long userId = 1L;
        Integer initialAmount = 100;
        Integer chargeAmount = null;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.of(userId, initialAmount)));
        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_CHARGE_AMOUNT.getCode(), ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void 잔액을_충전할때_충전금액_0보다_작거나_같으면_충전에_실패한다(Integer chargeAmount) {
        //given
        Long userId = 1L;
        Integer initialAmount = 100;
        UserCommand.Charge chargeCommand = new UserCommand.Charge(userId, chargeAmount);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.of(userId, initialAmount)));
        //when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.charge(chargeCommand));

        //then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_CHARGE_AMOUNT.getCode(), ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }


    @Test
    void 유저를_조회한다() {
        // given
        Long userId = 1L;
        Integer initialAmount = 100;
        UserCommand.Get command = new UserCommand.Get(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.of(userId, initialAmount)));

        // when
        User user = userService.getUser(command);

        // then
        assertThat(user)
            .extracting(User::getId, User::getAmount)
            .containsExactly(userId, initialAmount);
    }

    @Test
    void 유저를_조회할_때_사용자_식별자가_null이면_조회에_실패한다() {
        // given
        Long userId = null;
        Integer initialAmount = 100;
        UserCommand.Get command = new UserCommand.Get(userId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.getUser(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_USER_ID.getCode(), ErrorCode.INVALID_USER_ID.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void 유저를_조회할_때_사용자_식별자가_0보다_작거나_같으면_조회에_실패한다(Long userId) {
        // given
        Integer initialAmount = 100;
        UserCommand.Get command = new UserCommand.Get(userId);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.getUser(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_USER_ID.getCode(), ErrorCode.INVALID_USER_ID.getMessage());
    }

    @Test
    void 유저를_조회할_때_사용자가_없으면_조회에_실패한다() {
        // given
        Long userId = 1L;
        Integer initialAmount = 100;
        UserCommand.Get command = new UserCommand.Get(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> userService.getUser(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
    }

}
