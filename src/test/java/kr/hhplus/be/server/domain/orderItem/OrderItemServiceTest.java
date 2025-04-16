package kr.hhplus.be.server.domain.orderItem;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void 특정날에_완료된_주문내역을_조회한다() {
        // given
        LocalDate date = LocalDate.now();

        OrderItemCommand.GetCompletedByDate command = OrderItemCommand.GetCompletedByDate.of(date);

        Product product = Product.of(1L, "상품", 10, 10_000);
        OrderItem orderItem = OrderItem.create(product, 1);

        LocalDateTime startTime = command.getDate().atTime(0, 0, 0);
        LocalDateTime endTime = command.getDate().atTime(23, 59, 59, 999_999_999);

        Mockito.when(orderItemRepository.getCompletedWithinPeriod(startTime, endTime)).thenReturn(List.of(orderItem));

        // when
        List<OrderItem> result = orderItemService.getCompletedByDate(command);

        // then
        Assertions.assertThat(result)
            .hasSize(1)
            .extracting(OrderItem::getProduct, OrderItem::getOrderQuantity)
            .containsExactly(tuple(product, 1));

    }

    @Test
    void 완료된_주문내역을_조회할_때_날짜가_Null이면_INVALID_DATE_예외를_반환한다() {
        // given
        LocalDate date = null;

        OrderItemCommand.GetCompletedByDate command = OrderItemCommand.GetCompletedByDate.of(date);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderItemService.getCompletedByDate(command));

        // then
        Assertions.assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_DATE.getCode(), ErrorCode.INVALID_DATE.getMessage());
    }

}
