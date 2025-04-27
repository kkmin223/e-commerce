package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class UserServiceConcurrencyTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @Test
    void 동시에_같은_사용자의_잔액을_15명이_충전한다() {
        // given
        final int USER_THREADS = 15;

        User savedUser = userRepository.save(User.of(0));
        Integer chargeAmount = 1_000;

        ExecutorService executor = Executors.newFixedThreadPool(USER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < USER_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    userService.charge(UserCommand.Charge.of(savedUser.getId(), chargeAmount));
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        // 모든 작업이 끝날 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        User expectedUser = userRepository.findById(savedUser.getId()).get();

        Assertions.assertThat(expectedUser.getAmount())
            .isEqualTo(chargeAmount * (USER_THREADS - failCount.get()));
        System.out.println(expectedUser.getAmount());
    }
}
