package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponEventConsumer {

    private final CouponFacade couponFacade;

    @KafkaListener(
        topics = "COUPON",
        groupId = "coupon-group",
        concurrency = "3" // 파티션 수와 동일하게 설정 (기본 3개 파티션 가정)
    )
    public void handleCouponRequestEvent(ConsumerRecord<String, CouponEvent> record,
                                         Acknowledgment acknowledgment) {
        System.out.println("쿠폰 처리" + record.key() + record.value().getCouponId());
        couponFacade.IssueCoupon(CouponCriteria.Issue.of(record.value().getCouponId(), record.value().getUserId()));
        acknowledgment.acknowledge();
    }

}
