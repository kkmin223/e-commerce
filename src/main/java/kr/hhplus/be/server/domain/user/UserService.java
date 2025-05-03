package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import kr.hhplus.be.server.lock.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @DistributedLock(
        keys = "T(kr.hhplus.be.server.lock.LockKeyGenerator).generateForCharge(#command)"
    )
    @Transactional
    public User charge(UserCommand.Charge command) {
        if (command.getUserId() == null
            || command.getUserId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER_ID);
        }

        if (command.getChargeAmount() == null
            || command.getChargeAmount() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

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

    public User getUserForUpdate(UserCommand.Get command) {
        if (command.getUserId() == null
            || command.getUserId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_USER_ID);
        }

        return userRepository.findByIdForUpdate(command.getUserId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

}
