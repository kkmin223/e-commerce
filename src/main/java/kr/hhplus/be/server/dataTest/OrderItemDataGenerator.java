package kr.hhplus.be.server.dataTest;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import org.instancio.Instancio;
import org.instancio.Select;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class OrderItemDataGenerator {

    public List<User> generateUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> Instancio.of(User.class)
                .create())
            .toList();
    }

    public List<Product> generateProducts(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> Instancio.of(Product.class)
                .generate(Select.field(Product::getName), gen -> gen.text().pattern("P-10##"))

                .generate(Select.field(Product::getQuantity), gen -> gen.ints().range(0, 1000))
                .generate(Select.field(Product::getPrice), gen -> gen.ints().range(1000, 100_000))
                .create())
            .toList();
    }

    public List<Order> generateOrders(List<User> users, int perUser) {
        LocalDateTime baseDate = LocalDateTime.now().minusYears(1);

        return users.stream()
            .flatMap(user -> IntStream.range(0, perUser)
                .mapToObj(i -> Instancio.of(Order.class)
                    .set(Select.field("user"), user)
                    .generate(Select.field("orderAt"), gen -> gen.temporal()
                        .localDateTime()
                        .range(baseDate, baseDate.plus(1, ChronoUnit.YEARS))
                    )
                    .generate(Select.field("totalAmount"), gen -> gen.ints().range(10_000, 1_000_000))
                    .generate(Select.field("status"), gen -> gen.oneOf(OrderStatus.values()))
                    .create()))
            .toList();
    }

    public List<OrderItem> generateOrderItems(List<Order> orders, List<Product> products) {
        return orders.stream()
            .flatMap(order -> IntStream.range(0, ThreadLocalRandom.current().nextInt(1, 11))
                .mapToObj(i -> Instancio.of(OrderItem.class)
                    .set(Select.field("order"), order)
                    .set(Select.field("product"), randomProduct(products))
                    .generate(Select.field("orderQuantity"), gen -> gen.ints().range(1, 100))
                    .generate(Select.field("subTotalAmount"), gen -> gen.ints().range(10_000, 500_000))
                    .create()))
            .toList();
    }

    private Product randomProduct(List<Product> products) {
        return products.get(ThreadLocalRandom.current().nextInt(products.size()));
    }
}
