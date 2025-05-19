package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsCriteria;
import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsFacade;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.orderStatistics.OrderStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class StatisticsScheduler {

    private final OrderStatisticsFacade orderStatisticsFacade;
    private final OrderStatisticsService orderStatisticsService;
    private final CacheManager cacheManager;

    /**
     * 매일 00시 10분에 전날의 통계 데이터를 생성 -> 미사용
     */
    public void generateDailyOrderStatistics() {
        // 1. 전날의 통계 생성
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.minusDays(1); // 어제
        orderStatisticsFacade.generateStatisticsByDate(OrderStatisticsCriteria.GenerateStatisticsByDate.of(targetDate));

        // 2. 인기 판매 상품의 캐시 제거
        Cache topSellingProductIdsCache = cacheManager.getCache("TopSellingProductIds");

        if (topSellingProductIdsCache != null) {
            topSellingProductIdsCache.clear();
        }

        // 3. 인기 판매 상품의 캐시 최신화
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(5, today.minusDays(3), today);
        orderStatisticsService.getTopSellingProductIds(command);

    }

    /**
     * 매일 00시 10분에 전날의 통계 데이터를 생성
     */
    @Scheduled(cron = "0 10 0 * * *")
    public void generateStatisticsByDateWithRedis() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.minusDays(1); // 어제
        orderStatisticsFacade.generateStatisticsByDateWithRedis(OrderStatisticsCriteria.GenerateStatisticsByDate.of(targetDate));
    }
}
