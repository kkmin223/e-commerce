package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.instancio.Select.field;

class OrderTest {

    @Test
    void 사용자와_주문상품_수량과_시간을_입력하면_주문을_생성한다() {
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

        LocalDateTime now = LocalDateTime.now();

        // when
        Order order = Order.create(user, productQuantities, now);

        // then
        assertThat(order.getOrderItems())
            .hasSize(productQuantities.size())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactlyInAnyOrder(
                tuple(product1, orderQuantity1),
                tuple(product2, orderQuantity2)
            );

        assertThat(order)
            .extracting(Order::getTotalAmount, Order::getStatus, Order::getOrderAt)
            .containsExactly(product1.getPrice() * orderQuantity1 + product2.getPrice() * orderQuantity2, OrderStatus.PAYMENT_PENDING, now);
    }

    @Test
    void 사용자와_주문상품_수량과_시간을_입력하지_않아도_주문을_생성한다() {
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

        // when
        Order order = Order.create(user, productQuantities, null);

        // then
        assertThat(order.getOrderItems())
            .hasSize(productQuantities.size())
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactlyInAnyOrder(
                tuple(product1, orderQuantity1),
                tuple(product2, orderQuantity2)
            );

        assertThat(order)
            .extracting(Order::getTotalAmount, Order::getStatus)
            .containsExactly(product1.getPrice() * orderQuantity1 + product2.getPrice() * orderQuantity2, OrderStatus.PAYMENT_PENDING);
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

        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> Order.create(user, productQuantities, LocalDateTime.now()));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_STOCK.getCode(), ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    void 사용자가_null이면_주문_생성에_실패한다() {
        // given
        User user = null;
        Integer stockQuantity1 = 10;
        Product product1 = Product.of(1L, "상품1", stockQuantity1, 10_000);
        Integer orderQuantity1 = 11;

        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, orderQuantity1);

        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> Order.create(user, productQuantities, LocalDateTime.now()));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 주문상품이_null이면_주문_생성에_실패한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;


        Map<Product, Integer> productQuantities = null;

        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> Order.create(user, productQuantities, LocalDateTime.now()));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.ORDER_PRODUCT_NOT_FOUND.getCode(), ErrorCode.ORDER_PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void 주문상품이_비었으면_주문_생성에_실패한다() {
        // given
        User user = User.of(1L, 1_000_000);
        Integer stockQuantity1 = 10;

        Map<Product, Integer> productQuantities = new HashMap<>();

        // when
        BusinessLogicException exception = Assertions.assertThrows(BusinessLogicException.class, () -> Order.create(user, productQuantities, LocalDateTime.now()));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.ORDER_PRODUCT_NOT_FOUND.getCode(), ErrorCode.ORDER_PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    void 주문_상태를_완료로_바꾼다() {
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

        Order order = Order.create(user, productQuantities, LocalDateTime.now());
        // when
        order.completeOrder(product1.getPrice() * orderQuantity1 + product2.getPrice() * orderQuantity2);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void 주문_상태가_결제_대기인_경우_결제_가능_상태를_반환한다() {
        // given
        Order order = Instancio.of(Order.class)
            .set(field(Order::getStatus), OrderStatus.PAYMENT_PENDING)
            .create();

        // when
        boolean canPay = order.canPay();

        // then
        assertThat(canPay).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"COMPLETED", "FAILED"})
    void 주문_상태가_결제_대기가_아닌_경우_결제_불가능_상태를_반환한다(OrderStatus orderStatus) {
        // given
        Order order = Instancio.of(Order.class)
            .set(field(Order::getStatus), orderStatus)
            .create();

        // when
        boolean canPay = order.canPay();

        // then
        assertThat(canPay).isFalse();
    }

}
