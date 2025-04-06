package kr.hhplus.be.server.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User charge(UserCommand.Charge command) {
        User user = userRepository.getUser(command.getUserId());

        user.chargeAmount(command.getChargeAmount());

        return user;
    }

}
