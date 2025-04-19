package kr.hhplus.be.server.domain.orderStatistics;

import java.time.LocalDate;
import java.util.List;

public interface OrderStatisticsRepository {
    List<OrderStatistics> saveAll(List<OrderStatistics> orderStatisticsList);
    List<Long> findTopSellingProductIds(LocalDate startDate, LocalDate endDate, Integer count);
    List<OrderStatistics> findByStatisticDate(LocalDate date);
}
