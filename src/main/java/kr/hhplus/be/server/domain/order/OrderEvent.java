package kr.hhplus.be.server.domain.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderEvent {

    private Long orderId;

    public static OrderEvent of(Long orderId) {
        return new OrderEvent(orderId);
    }
}
