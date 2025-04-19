package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.domain.orderItem.OrderItemService;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class OrderStatisticsFacadeTest {

    @InjectMocks
    private OrderStatisticsFacade orderStatisticsFacade;

    @Mock
    private OrderStatisticsService orderStatisticsService;

    @Mock
    private OrderItemService orderItemService;

    @Test
    void 날짜에_해당하는_상품_판매_통계를_생성한다() {
        // given
        OrderStatisticsCriteria.GenerateStatisticsByDate criteria = OrderStatisticsCriteria.GenerateStatisticsByDate.of(LocalDate.now());

        // when
        orderStatisticsFacade.generateStatisticsByDate(criteria);

        // then
        InOrder inOrder = Mockito.inOrder(orderStatisticsService, orderItemService);
        inOrder.verify(orderItemService).getCompletedByDate(Mockito.any());
        inOrder.verify(orderStatisticsService).generateDailyStatistics(Mockito.any());
    }
}
