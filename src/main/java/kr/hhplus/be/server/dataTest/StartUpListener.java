package kr.hhplus.be.server.dataTest;

import jakarta.persistence.EntityManagerFactory;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.couponItem.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class StartUpListener {

    private final SessionFactory sessionFactory;

    @Autowired
    public StartUpListener(EntityManagerFactory emf) {
        // Hibernate SessionFactory 추출
        this.sessionFactory = emf.unwrap(SessionFactory.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("데이터 입력 시작");
        //유저_보유_쿠폰_데이터_입력();
        //주문_내역_데이터_입력();
        //주문_통계_데이터_입력();
        System.out.println("데이터 입력 완료");
    }

    private void 유저_보유_쿠폰_데이터_입력() {
        final int USER_COUNT = 10_000;
        final int COUPON_COUNT = 1_000;
        final int ITEM_PER_USER = 100;
        final int TOP_USER_COUNT = 10; // 상위 10명 사용자
        final int ITEM_PER_TOP_USER = 100_000; // 1인당 10만
        // 데이터 생성
        DataGenerator generator = new DataGenerator();
        List<User> users = generator.generateUsers(USER_COUNT);
        List<Coupon> coupons = generator.generateCoupons(COUPON_COUNT);
        List<CouponItem> items = generator.generateTopUserCouponItems(users, coupons, TOP_USER_COUNT, ITEM_PER_TOP_USER);

        // 배치 삽입 실행
        try (StatelessSession session = sessionFactory.openStatelessSession()) {
            session.getTransaction().begin();

            insertEntities(session, users, "User");
            insertEntities(session, coupons, "Coupon");
            insertEntities(session, items, "CouponItem");

            session.getTransaction().commit();
        }
    }

    private void 주문_내역_데이터_입력() {
        final int USER_COUNT = 100;
        final int PRODUCT_COUNT = 500;
        final int ORDERS_PER_USER = 100;
        OrderItemDataGenerator generator = new OrderItemDataGenerator();

        List<User> users = generator.generateUsers(USER_COUNT);
        List<Product> products = generator.generateProducts(PRODUCT_COUNT);
        List<Order> orders = generator.generateOrders(users, ORDERS_PER_USER);
        List<OrderItem> items = generator.generateOrderItems(orders, products);

        // 배치 삽입 실행
        try (StatelessSession session = sessionFactory.openStatelessSession()) {
            session.getTransaction().begin();

            insertEntities(session, users, "User");
            insertEntities(session, products, "Product");
            insertEntities(session, orders, "Order");
            insertEntities(session, items, "OrderItem");

            session.getTransaction().commit();
        }
    }

    private void 주문_통계_데이터_입력() {
        // 2025-04-01 ~ 2025-04-30 기간 데이터 생성
        List<OrderStatistics> stats = IntStream.range(0, 50_000)
            .mapToObj(i -> Instancio.of(OrderStatistics.class)
                .generate(Select.field("statisticDate"), gen -> gen.temporal()
                    .localDate()
                    .range(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30)))
                .generate(Select.field("productId"), gen -> gen.longs().range(1L, 500L))
                .generate(Select.field("soldQuantity"), gen -> gen.ints().range(1, 100))
                .create())
            .toList();

        // 배치 삽입
        try (StatelessSession session = sessionFactory.openStatelessSession()) {
            session.getTransaction().begin();

            final int BATCH_SIZE = 1000;
            for (int i = 0; i < stats.size(); i++) {
                session.insert(stats.get(i));
                if (i % BATCH_SIZE == 0 && i > 0) {
                    session.getTransaction().commit();
                    session.getTransaction().begin();
                }
            }
            session.getTransaction().commit();
        }
    }

    private static <T> void insertEntities(StatelessSession session, List<T> entities, String label) {
        System.out.println("Inserting " + entities.size() + " " + label);
        entities.forEach(session::insert);
        System.out.println("Inserting Complete " + entities.size() + " " + label);
    }
}






