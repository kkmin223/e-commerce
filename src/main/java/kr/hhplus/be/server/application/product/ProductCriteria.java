package kr.hhplus.be.server.application.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ProductCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetTopSellingProducts {
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer count;

        public static GetTopSellingProducts of(LocalDate startDate, LocalDate endDate, Integer count) {
            return new GetTopSellingProducts(startDate, endDate, count);
        }
    }
}
