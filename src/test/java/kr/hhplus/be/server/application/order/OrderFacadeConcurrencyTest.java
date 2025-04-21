package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.AmountCoupon;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.couponItem.CouponItemRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderFacadeConcurrencyTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE coupon");
        jdbcTemplate.execute("TRUNCATE TABLE coupon_item");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("TRUNCATE TABLE product");
        jdbcTemplate.execute("TRUNCATE TABLE orders");
        jdbcTemplate.execute("TRUNCATE TABLE payment");
        jdbcTemplate.execute("TRUNCATE TABLE order_item");
        jdbcTemplate.execute("TRUNCATE TABLE order_statistics");
    }

    @Test
    void 동시에_15명이_재고10개_상품을_주문한다() throws Exception {
        // Given
        final int INITIAL_STOCK = 1;
        final int ORDER_THREADS = 2;

        Product product = productRepository.save(
            Product.of(null, "한정판 상품", INITIAL_STOCK, 10_000)
        );
        List<User> users = createTestUsers(ORDER_THREADS);

        ExecutorService executor = Executors.newFixedThreadPool(ORDER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // When
        for (User user : users) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
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
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getQuantity()).isZero();

    }

    @Test
    void 동시에_같은_쿠폰을_5번_사용하면_1번만_성공한다() throws Exception {
        // given
        final int ORDER_THREADS = 5;

        User savedUser = userRepository.save(User.of(100_000));
        Coupon savedCoupon = couponRepository.save(AmountCoupon.of("쿠폰", 1, 100));
        CouponItem savedCouponItem = couponItemRepository.save(CouponItem.of(savedUser, savedCoupon, false));

        Product product = productRepository.save(
            Product.of(null, "상품", 100, 10_000)
        );

        ExecutorService executor = Executors.newFixedThreadPool(ORDER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < ORDER_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    OrderCriteria.OrderProduct orderProduct =
                        OrderCriteria.OrderProduct.of(product.getId(), 1);
                    OrderCriteria.OrderAndPay criteria =
                        OrderCriteria.OrderAndPay.of(savedUser.getId(), savedCouponItem.getId(), List.of(orderProduct), LocalDateTime.now());

                    orderFacade.orderAndPay(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외 기록
                    failCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(10, TimeUnit.SECONDS); // 타임아웃 설정

        // Then
        CouponItem couponItem = couponItemRepository.findById(savedCouponItem.getId()).get();
        assertThat(couponItem.getIsUsed())
            .isEqualTo(true);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(ORDER_THREADS - 1);
    }

    @Test
    void 동시에_같은_사용자가_5번의_주문을_진행하면_모든_결제금액에_대해_잔액이_차감된다() throws Exception {
        // given
        final int ORDER_THREADS = 5;

        Product product = productRepository.save(
            Product.of(null, "상품", 100, 10_000)
        );

        User savedUser = userRepository.save(User.of(product.getPrice() * ORDER_THREADS));

        ExecutorService executor = Executors.newFixedThreadPool(ORDER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < ORDER_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {

                    OrderCriteria.OrderProduct orderProduct =
                        OrderCriteria.OrderProduct.of(product.getId(), 1);
                    OrderCriteria.OrderAndPay criteria =
                        OrderCriteria.OrderAndPay.of(savedUser.getId(), null, List.of(orderProduct), LocalDateTime.now());

                    orderFacade.orderAndPay(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외 기록
                    failCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(10, TimeUnit.SECONDS); // 타임아웃 설정

        // Then
        assertThat(successCount.get()).isEqualTo(ORDER_THREADS);
        assertThat(failCount.get()).isEqualTo(0);

        User user = userRepository.findById(savedUser.getId()).get();
        assertThat(user.getAmount())
            .isZero();
    }

    @Test
    void 동시에_같은_사용자가_5번의_주문을_진행할떄_결제금액이_잔액보다_큰경우_실패한다() throws Exception {
        // given
        final int ORDER_THREADS = 5;

        Product product = productRepository.save(
            Product.of(null, "상품", 100, 10_000)
        );

        User savedUser = userRepository.save(User.of(product.getPrice() * (ORDER_THREADS - 2)));

        ExecutorService executor = Executors.newFixedThreadPool(ORDER_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < ORDER_THREADS; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    OrderCriteria.OrderProduct orderProduct =
                        OrderCriteria.OrderProduct.of(product.getId(), 1);
                    OrderCriteria.OrderAndPay criteria =
                        OrderCriteria.OrderAndPay.of(savedUser.getId(), null, List.of(orderProduct), LocalDateTime.now());

                    orderFacade.orderAndPay(criteria);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외 기록
                    failCount.incrementAndGet();
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .get(10, TimeUnit.SECONDS); // 타임아웃 설정

        // Then
        assertThat(successCount.get()).isEqualTo(3);
        assertThat(failCount.get()).isEqualTo(2);

        User user = userRepository.findById(savedUser.getId()).get();
        assertThat(user.getAmount())
            .isZero();
    }

    private List<User> createTestUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> userRepository.save(User.of(1_000_000)))
            .toList();
    }
}
