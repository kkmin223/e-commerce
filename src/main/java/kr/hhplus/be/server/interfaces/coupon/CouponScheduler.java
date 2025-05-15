package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.orderStatistics.OrderStatisticsCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class CouponScheduler {

    private final CouponFacade couponFacade;

    /**
     * 매분에 쿠폰 발급
     */
    @Scheduled(cron = "0 * * * * *")
    public void generateStatisticsByDateWithRedis() {
        couponFacade.processCouponRequest(CouponCriteria.Process.of(10));
    }

}
