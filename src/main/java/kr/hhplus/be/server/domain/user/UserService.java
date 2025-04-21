package kr.hhplus.be.server.domain.user;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Retryable(
        value = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class}, // 재시도할 예외
        maxAttempts = 5,
        backoff = @Backoff(delay = 100), // 재시도 간 딜레이(ms),
        recover = "recoverCharge"
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

        User user = userRepository.findByIdWithOptimisticLock(command.getUserId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        user.chargeAmount(command.getChargeAmount());

        return user;
    }

    @Recover
    public User recoverCharge(Exception e, UserCommand.Charge command) {
        throw new BusinessLogicException(ErrorCode.CONCURRENCY_CHARGE_USER);
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
