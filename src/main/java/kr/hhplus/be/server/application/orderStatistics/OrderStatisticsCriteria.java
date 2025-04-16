package kr.hhplus.be.server.application.orderStatistics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class OrderStatisticsCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GenerateStatisticsByDate {
        private LocalDate statisticDate;

        public static GenerateStatisticsByDate of(LocalDate statisticDate) {
            return new GenerateStatisticsByDate(statisticDate);
        }
    }
}
