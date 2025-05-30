package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    void 재고를_차감한다() {
        // given
        Integer initialQuantity = 10;
        Product product1 = Product.of(1L, "상품1", initialQuantity, 1_000);
        Integer reduceQuantity = 1;

        // when
        product1.reduceQuantity(reduceQuantity);

        // then
        assertThat(product1.getQuantity()).isEqualTo(initialQuantity - reduceQuantity);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 차감하는_재고_수량이_0_보다_작거나_같을_경우_재고차감에_실패한다(Integer reduceQuantity) {
        // given
        Integer initialQuantity = 10;
        Product product1 = Product.of(1L, "상품1", initialQuantity, 1_000);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> product1.reduceQuantity(reduceQuantity));

        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_REDUCE_QUANTITY.getCode(), ErrorCode.INVALID_REDUCE_QUANTITY.getMessage());
    }

    @Test
    void 차감_이후_수량이_0보다_작을_경우_재고차감에_실패한다() {
        // given
        Integer initialQuantity = 10;
        Product product1 = Product.of(1L, "상품1", initialQuantity, 1_000);
        Integer reduceQuantity = initialQuantity + 1;

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> product1.reduceQuantity(reduceQuantity));

        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INSUFFICIENT_STOCK.getCode(), ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @Test
    void 재고를_증가시킨다() {
        // given
        Integer initialQuantity = 10;
        Product product1 = Product.of(1L, "상품1", initialQuantity, 1_000);
        Integer increaseQuantity = 1;

        // when
        product1.increaseQuantity(increaseQuantity);

        // then
        assertThat(product1.getQuantity()).isEqualTo(initialQuantity + increaseQuantity);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 증가하는_재고_수량이_0_보다_작거나_같을_경우_재고증가에_실패한다(Integer increaseQuantity) {
        // given
        Integer initialQuantity = 10;
        Product product1 = Product.of(1L, "상품1", initialQuantity, 1_000);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> product1.increaseQuantity(increaseQuantity));

        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_INCREASE_QUANTITY.getCode(), ErrorCode.INVALID_INCREASE_QUANTITY.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void 주문_수량보다_재고_수량이_많거나_같으면_true를_반환한다(Integer orderQuantity) {
        // given
        Integer initialQuantity = 10;
        Product product = Product.of(1L, "상품1", initialQuantity, 1_000);

        // when
        boolean result = product.canOrder(orderQuantity);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 주문_수량보다_재고_수량이_적으면_false를_반환한다() {
        // given
        Integer initialQuantity = 10;
        Product product = Product.of(1L, "상품1", initialQuantity, 1_000);
        Integer orderQuantity = initialQuantity + 1;

        // when
        boolean result = product.canOrder(orderQuantity);

        // then
        assertThat(result).isFalse();
    }
}
