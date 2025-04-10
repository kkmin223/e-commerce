package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientCouponQuantityException;
import kr.hhplus.be.server.interfaces.coupon.CouponRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon getIssuableCoupon(CouponCommand.Get command) {
        Coupon coupon = couponRepository.getCoupon(command.getCouponId());

        if (!coupon.canIssue()) {
            throw new InsufficientCouponQuantityException();
        }

        return coupon;
    }

}
