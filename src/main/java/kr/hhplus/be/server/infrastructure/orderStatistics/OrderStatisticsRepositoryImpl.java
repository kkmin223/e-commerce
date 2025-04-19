package kr.hhplus.be.server.infrastructure.orderStatistics;

import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class OrderStatisticsRepositoryImpl implements OrderStatisticsRepository {

    private final OrderStatisticsJpaRepository orderStatisticsJpaRepository;

    public List<OrderStatistics> saveAll(List<OrderStatistics> orderStatisticsList) {
        return orderStatisticsJpaRepository.saveAll(orderStatisticsList);
    }

    @Override
    public List<Long> findTopSellingProductIds(LocalDate startDate, LocalDate endDate, Integer count) {
        Pageable pageable = PageRequest.of(0, count);

        return orderStatisticsJpaRepository.findTopSellingProductIdsBetween(startDate, endDate, pageable);
    }

    @Override
    public List<OrderStatistics> findByStatisticDate(LocalDate date) {
        return orderStatisticsJpaRepository.findByStatisticDate(date);
    }
}
