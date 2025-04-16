package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderStatisticsServiceTest {

    @InjectMocks
    private OrderStatisticsService orderStatisticsService;

    @Mock
    private OrderStatisticsRepository orderStatisticsRepository;

    @Test
    void 날짜를_입력받아_상품_통계를_생성한다() {
        // given
        LocalDate statisticDate = LocalDate.now();
        Product product = Product.of(1L, "상품", 10, 1_000);
        OrderItem orderItem = OrderItem.create(product, 1);

        OrderStatisticsCommand.GenerateDailyStatistics command = OrderStatisticsCommand.GenerateDailyStatistics.of(statisticDate, List.of(orderItem));

        // when
        orderStatisticsService.generateDailyStatistics(command);


        // then
        Mockito.verify(orderStatisticsRepository, Mockito.times(1)).saveAll(Mockito.any());
    }

    @Test
    void 상품_통계를_생성할_때_날짜가_null이면_INVALID_DATE_예외가_발생한다() {
        // given
        LocalDate statisticDate = null;
        Product product = Product.of(1L, "상품", 10, 1_000);
        OrderItem orderItem = OrderItem.create(product, 1);

        OrderStatisticsCommand.GenerateDailyStatistics command = OrderStatisticsCommand.GenerateDailyStatistics.of(statisticDate, List.of(orderItem));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderStatisticsService.generateDailyStatistics(command));


        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_DATE.getCode(), ErrorCode.INVALID_DATE.getMessage());

        Mockito.verify(orderStatisticsRepository, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void 통계기반으로_판매량이_많은_상품_ID를_조회한다() {
        // given
        int count = 5;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(3);
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(count, startDate, endDate);

        List<Long> productIds = List.of(1L, 2L, 3L, 4L, 5L);
        Mockito.when(orderStatisticsRepository.findTopSellingProductIds(startDate, endDate, count)).thenReturn(productIds);

        // when
        List<Long> topSellingProductIds = orderStatisticsService.getTopSellingProductIds(command);

        // then
        assertThat(topSellingProductIds).containsExactly(1L, 2L, 3L, 4L, 5L);

    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void 통계기반으로_판매량이_많은_상품_ID를_조회할_떄_개수가_0이하이면_INVLID_GET_COUNT_예외가_발생한다(int count) {
        // given
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(3);
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(count, startDate, endDate);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderStatisticsService.getTopSellingProductIds(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_GET_COUNT.getCode(), ErrorCode.INVALID_GET_COUNT.getMessage());
    }

    @Test
    void 통계기반으로_판매량이_많은_상품_ID를_조회할_떄_시작날짜가_종료날짜보다_크면_INVLID_DATE_예외가_발생한다() {
        // given
        int count = 5;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.plusDays(3);
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(count, startDate, endDate);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderStatisticsService.getTopSellingProductIds(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_DATE.getCode(), ErrorCode.INVALID_DATE.getMessage());
    }

    @Test
    void 통계기반으로_판매량이_많은_상품_ID를_조회할_떄_시작날짜가_null이면_INVLID_DATE_예외가_발생한다() {
        // given
        int count = 5;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = null;
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(count, startDate, endDate);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderStatisticsService.getTopSellingProductIds(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_DATE.getCode(), ErrorCode.INVALID_DATE.getMessage());
    }

    @Test
    void 통계기반으로_판매량이_많은_상품_ID를_조회할_떄_종료날짜가_null이면_INVLID_DATE_예외가_발생한다() {
        // given
        int count = 5;
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = null;
        OrderStatisticsCommand.GetTopSellingProductIds command = OrderStatisticsCommand.GetTopSellingProductIds.of(count, startDate, endDate);

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> orderStatisticsService.getTopSellingProductIds(command));

        // then
        assertThat(exception)
            .extracting(BusinessLogicException::getCode, BusinessLogicException::getMessage)
            .containsExactly(ErrorCode.INVALID_DATE.getCode(), ErrorCode.INVALID_DATE.getMessage());
    }

}
