package kr.hhplus.be.server.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Transactional
@SpringBootTest
public class UserServiceConcurrencyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 동시에_같은_사용자의_잔액을_15명이_충전한다() {
        // given
        final int USER_THREADS = 15;

        User savedUser = userRepository.save(User.of(0));
        Integer chargeAmount = 1_000;

        CountDownLatch latch = new CountDownLatch(USER_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(USER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < USER_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    latch.countDown();
                    latch.await(); // 모든 스레드 동시 실행

                    userService.charge(UserCommand.Charge.of(savedUser.getId(), chargeAmount));
                } catch (Exception e) {
                    // 예외 기록
                }
            }, executor);
            futures.add(future);
        }
        // then
        User expectedUser = userRepository.findById(savedUser.getId()).get();

        Assertions.assertThat(expectedUser.getAmount())
            .isEqualTo(chargeAmount * USER_THREADS);
    }


}
