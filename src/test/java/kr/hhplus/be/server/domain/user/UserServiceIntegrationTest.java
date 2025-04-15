package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 잔액을_충전한다() {
        // given
        User savedUser = userRepository.save(User.of(0));

        UserCommand.Charge command = UserCommand.Charge.of(savedUser.getId(), 1_000);

        // when
        User user = userService.charge(command);

        // then
        assertThat(user)
            .extracting(User::getId, User::getAmount)
            .containsExactly(savedUser.getId(), 1_000);
    }

    @Test
    void 사용자를_조회한다() {
        // given
        User savedUser = userRepository.save(User.of(1000));

        UserCommand.Get command = UserCommand.Get.of(savedUser.getId());

        // when
        User user = userService.getUser(command);

        // then
        assertThat(user)
            .extracting(User::getId, User::getAmount)
            .containsExactly(savedUser.getId(), 1_000);
    }

}
