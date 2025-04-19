package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.orderItem.OrderItem;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;

    public void generateDailyStatistics(OrderStatisticsCommand.GenerateDailyStatistics command) {

        if (command.getStatisticDate() == null) {
            throw new BusinessLogicException(ErrorCode.INVALID_DATE);
        }

        Map<Product, Integer> soldQuantities = new HashMap<>();

        for (OrderItem orderItem : command.getOrderItems()) {
            Product product = orderItem.getProduct();
            soldQuantities.put(product, soldQuantities.getOrDefault(product, 0) + orderItem.getOrderQuantity());
        }

        List<OrderStatistics> statisticsList = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : soldQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer soldQuantity = entry.getValue();

            OrderStatistics orderStatistics = OrderStatistics.of(command.getStatisticDate(), product, soldQuantity);
            statisticsList.add(orderStatistics);
        }

        orderStatisticsRepository.saveAll(statisticsList);
    }

    public List<Long> getTopSellingProductIds(OrderStatisticsCommand.GetTopSellingProductIds command) {
        if (command.getCount() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_GET_COUNT);
        }

        if (command.getStartDate() == null
            || command.getEndDate() == null
            || command.getStartDate().isAfter(command.getEndDate())) {
            throw new BusinessLogicException(ErrorCode.INVALID_DATE);
        }

        return orderStatisticsRepository.findTopSellingProductIds(command.getStartDate(), command.getEndDate(), command.getCount());
    }
}
