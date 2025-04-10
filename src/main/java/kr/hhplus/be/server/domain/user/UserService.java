package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.exceptions.InvalidChargeAmountException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidUserIdException;
import kr.hhplus.be.server.interfaces.common.exceptions.UserNotFoundException;
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
            throw new InvalidUserIdException();
        }

        User user = userRepository.getUser(command.getUserId())
            .orElseThrow(UserNotFoundException::new);

        if (command.getChargeAmount() == null
            || command.getChargeAmount() <= 0) {
            throw new InvalidChargeAmountException();
        }

        user.chargeAmount(command.getChargeAmount());

        return user;
    }

    public User getUser(UserCommand.Get command) {
        if (command.getUserId() == null
            || command.getUserId() <= 0) {
            throw new InvalidUserIdException();
        }

        return userRepository.getUser(command.getUserId())
            .orElseThrow(UserNotFoundException::new);
    }

}
