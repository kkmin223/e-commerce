package kr.hhplus.be.server.domain.orderItem;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getCompletedByDate(OrderItemCommand.GetCompletedByDate command) {
        if (command.getDate() == null) {
            throw new BusinessLogicException(ErrorCode.INVALID_DATE);
        }

        LocalDateTime startTime = command.getDate().atTime(0, 0, 0);
        LocalDateTime endTime = command.getDate().atTime(23, 59, 59, 999_999_999);

        return orderItemRepository.getCompletedWithinPeriod(startTime, endTime);
    }
}
