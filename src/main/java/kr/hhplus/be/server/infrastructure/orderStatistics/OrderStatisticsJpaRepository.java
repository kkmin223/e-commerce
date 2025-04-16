package kr.hhplus.be.server.infrastructure.orderStatistics;


import kr.hhplus.be.server.domain.orderStatistics.OrderStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderStatisticsJpaRepository extends JpaRepository<OrderStatistics, Long> {

    @Query("""
        SELECT os.productId
        FROM OrderStatistics os
        WHERE os.statisticDate BETWEEN :startDate AND :endDate
        GROUP BY os.productId
        ORDER BY SUM(os.soldQuantity) DESC
        """)
    List<Long> findTopSellingProductIdsBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    List<OrderStatistics> findByStatisticDate(LocalDate date);
}
