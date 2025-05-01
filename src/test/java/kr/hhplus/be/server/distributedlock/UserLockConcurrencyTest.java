package kr.hhplus.be.server.distributedlock;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCommand;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class UserLockConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE orders");
        jdbcTemplate.execute("TRUNCATE TABLE payment");
        jdbcTemplate.execute("TRUNCATE TABLE order_item");
    }

    @Test
    void 잔액_충전과_주문_결제를_동시에_진행한다() {
        // given
        Integer initialAmount = 10_000;
        Integer chargeAmount = 10_000;
        User savedUser = userRepository.save(User.of(initialAmount));
        Integer productPrice = 5_000;
        Product savedProduct = productRepository.save(Product.of(null, "상품1", 10, productPrice));

        final int THREAD_COUNT = 2;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        CompletableFuture<Void> orderFuture = CompletableFuture.runAsync(() -> {
            try {
                OrderCriteria.OrderProduct orderProduct =
                    OrderCriteria.OrderProduct.of(savedProduct.getId(), 1);
                OrderCriteria.OrderAndPay orderCriteria =
                    OrderCriteria.OrderAndPay.of(savedUser.getId(), null, List.of(orderProduct), LocalDateTime.now());

                orderFacade.orderAndPay(orderCriteria);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);

        CompletableFuture<Void> chargeFuture = CompletableFuture.runAsync(() -> {
            try {
                UserCommand.Charge chargeCommand = UserCommand.Charge.of(savedUser.getId(), chargeAmount);
                userService.charge(chargeCommand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);

        futures.add(orderFuture);
        futures.add(chargeFuture);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        User expectedUser = userRepository.findById(savedUser.getId()).get();
        Assertions.assertThat(expectedUser.getAmount())
            .isEqualTo(initialAmount - productPrice + chargeAmount);
    }


}
