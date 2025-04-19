package kr.hhplus.be.server.domain.orderItem;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class OrderItemCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetCompletedByDate {
        private LocalDate date;

        public static GetCompletedByDate of(LocalDate date) {
            return new GetCompletedByDate(date);
        }
    }
}
