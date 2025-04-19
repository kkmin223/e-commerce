package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class OrderStatisticsCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GenerateDailyStatistics {
        private LocalDate statisticDate;
        private List<OrderItem> orderItems;

        public static GenerateDailyStatistics of(LocalDate statisticDate, List<OrderItem> orderItems) {
            return new GenerateDailyStatistics(statisticDate, orderItems);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetTopSellingProductIds {
        private int count;
        private LocalDate startDate;
        private LocalDate endDate;

        public static GetTopSellingProductIds of(int count, LocalDate startDate, LocalDate endDate) {
            return new GetTopSellingProductIds(count, startDate, endDate);
        }
    }
}
