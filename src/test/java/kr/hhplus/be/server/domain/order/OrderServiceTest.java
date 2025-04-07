package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void 주문을_생성한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 1;

        Integer stockQuantity2 = 10;
        Product product2 = Product.of(2L, "상품2", stockQuantity2, 20_000);
        Integer orderQuantity2 = 2;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);
        productQuantities.put(product2, orderQuantity2);

        LocalDateTime orderAt = LocalDateTime.now();

        OrderCommand.CreateOrder command = OrderCommand.CreateOrder.of(user, productQuantities, orderAt);

        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(Order.create(user, productQuantities, orderAt));

        // when
        Order order = orderService.createOrder(command);

        // then
        assertThat(order.getOrderItems())
            .hasSize(productQuantities.size())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactlyInAnyOrder(
                tuple(product1, orderQuantity1),
                tuple(product2, orderQuantity2)
            );

        assertThat(order)
            .extracting(Order::getTotalAmount, Order::getOrderAt, Order::getStatus)
            .containsExactly(product1.getPrice() * orderQuantity1 + product2.getPrice() * orderQuantity2, orderAt, OrderStatus.PAYMENT_PENDING);
    }

    @Test
    void 주문상품_수량이_상품_재고수량보다_많으면_주문_생성을_실패한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 11;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);

        LocalDateTime orderAt = LocalDateTime.now();

        OrderCommand.CreateOrder command = OrderCommand.CreateOrder.of(user, productQuantities, orderAt);

        // when
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> orderService.createOrder(command));

        // then
        assertThat(exception)
            .extracting(InsufficientStockException::getCode, InsufficientStockException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_STOCK.getCode(), ErrorCode.INSUFFICIENT_STOCK.getMessage());

        verify(orderRepository, never()).save(any(Order.class));
    }
}
