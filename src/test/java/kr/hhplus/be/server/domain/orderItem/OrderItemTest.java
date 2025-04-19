package kr.hhplus.be.server.domain.orderItem;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    @Test
    void 상품과_수량을_입력하면_주문항목을_생성하고_상품재고를_차감한다() {
        // given
        Integer stockQuantity = 10;
        Product product = Product.of(1L, "상품", stockQuantity, 10_000);
        Integer orderQuantity = 5;

        // when
        OrderItem orderItem = OrderItem.create(product, orderQuantity);

        // then
        assertThat(orderItem)
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity, OrderItem::getSubTotalAmount)
            .containsExactly(product, orderQuantity, product.getPrice() * orderQuantity);

        assertThat(product.getQuantity())
            .isEqualTo(stockQuantity - orderQuantity);
    }

    @Test
    void 상품_재고_수량보다_주문_수량이_많을경우_주문항목_생성을_실패한다() {
        // given
        Integer stockQuantity = 10;
        Product product = Product.of(1L, "상품", stockQuantity, 10_000);
        Integer orderQuantity = stockQuantity + 1;

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> OrderItem.create(product, orderQuantity));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_STOCK.getCode(), ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

}
