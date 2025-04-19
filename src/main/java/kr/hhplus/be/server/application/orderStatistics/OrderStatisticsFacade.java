package kr.hhplus.be.server.application.orderStatistics;

import kr.hhplus.be.server.application.common.Facade;
import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.orderItem.OrderItemCommand;
import kr.hhplus.be.server.domain.orderItem.OrderItemService;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Facade
public class OrderStatisticsFacade {

    private final OrderItemService orderItemService;
    private final OrderStatisticsService orderStatisticsService;

    @Transactional
    public void generateStatisticsByDate(OrderStatisticsCriteria.GenerateStatisticsByDate criteria) {
        List<OrderItem> orderItems = orderItemService.getCompletedByDate(OrderItemCommand.GetCompletedByDate.of(criteria.getStatisticDate()));

        orderStatisticsService.generateDailyStatistics(OrderStatisticsCommand.GenerateDailyStatistics.of(criteria.getStatisticDate(), orderItems));
    }

}
