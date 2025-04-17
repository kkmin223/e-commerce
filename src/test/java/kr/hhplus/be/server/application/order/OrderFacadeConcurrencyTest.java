package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest
public class OrderFacadeConcurrencyTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 동시에_15명이_재고10개_상품을_주문한다() throws Exception {
        // Given
        final int INITIAL_STOCK = 10;
        final int ORDER_THREADS = 15;

        Product product = productRepository.save(
            Product.of(null, "한정판 상품", INITIAL_STOCK, 10_000)
        );
        List<User> users = createTestUsers(ORDER_THREADS);

        CountDownLatch latch = new CountDownLatch(ORDER_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(ORDER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // When
        for (User user : users) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    latch.countDown();
                    latch.await(); // 모든 스레드 동시 실행

                    OrderCriteria.OrderProduct orderProduct =
                        OrderCriteria.OrderProduct.of(product.getId(), 1);
                    OrderCriteria.OrderAndPay criteria =
                        OrderCriteria.OrderAndPay.of(user.getId(), null, List.of(orderProduct), LocalDateTime.now());

                    orderFacade.orderAndPay(criteria);
                } catch (Exception e) {
                    // 예외 기록
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(10, TimeUnit.SECONDS); // 타임아웃 설정

        // Then
        Product updatedProduct = productRepository.getProduct(product.getId()).orElseThrow();
        assertThat(updatedProduct.getQuantity()).isZero();

    }

    private List<User> createTestUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> userRepository.save(User.of(1_000_000)))
            .toList();
    }
}
