package kr.hhplus.be.server.scheduler;

import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsCriteria;
import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class StatisticsScheduler {

    private final OrderStatisticsFacade orderStatisticsFacade;

    /**
     * 매일 00시 10분에 전날의 통계 데이터를 생성
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void generateDailyOrderStatistics() {
        LocalDate targetDate = LocalDate.now().minusDays(1); // 어제
        orderStatisticsFacade.generateStatisticsByDate(OrderStatisticsCriteria.GenerateStatisticsByDate.of(targetDate));
    }
}
