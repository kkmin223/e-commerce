package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final KafkaTemplate<String, CouponEvent> kafkaTemplate;

    public Coupon getIssuableCoupon(CouponCommand.Get command) {
        if (command.getCouponId() == null
            || command.getCouponId() <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_COUPON_ID);
        }

        Coupon coupon = couponRepository.findById(command.getCouponId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.COUPON_NOT_FOUND));

        if (!coupon.canIssue()) {
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }

        return coupon;
    }

    public void requestCoupon(CouponCommand.Request command) {
        couponRepository.requestCoupon(command.getCouponId(), command.getUserId(), command.getIssuedAt());
    }

    public Set<String> getIssueRequest(CouponCommand.GetIssueRequest command) {
        return couponRepository.getIssueRequest(command.getCouponId(), command.getIssueQuantity());
    }

    public List<Coupon> getAllIssuableCoupons() {
        return couponRepository.getAllIssuableCoupons();
    }

    public void deleteIssueRequest(CouponCommand.DeleteIssueRequest command) {
        couponRepository.deleteIssueRequest(command.getCouponId(), command.getUserId());
    }

    public boolean isDuplicatedUser(CouponCommand.IsDuplicatedUser command) {
        return couponRepository.isDuplicatedUser(command.getCouponId(), command.getUserId());
    }

    public void sendCouponEvent(CouponCommand.Request command) {
        kafkaTemplate.send("COUPON"
            , String.valueOf((command.getCouponId() % 3))
            , CouponEvent.of(command.getCouponId(), command.getUserId()));
    }
}
