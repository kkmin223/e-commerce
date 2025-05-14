package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

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

}
