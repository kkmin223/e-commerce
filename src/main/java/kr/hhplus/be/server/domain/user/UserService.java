package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User charge(UserCommand.Charge command) {
        if (command.getUserId() == null
            || command.getUserId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER_ID);
        }

        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        if (command.getChargeAmount() == null
            || command.getChargeAmount() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        user.chargeAmount(command.getChargeAmount());

        return user;
    }

    public User getUser(UserCommand.Get command) {
        if (command.getUserId() == null
            || command.getUserId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER_ID);
        }

        return userRepository.findById(command.getUserId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

}
