package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.exceptions.CouponNotFoundException;
import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientCouponQuantityException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidCouponIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public Coupon getIssuableCoupon(CouponCommand.Get command) {
        if (command.getCouponId() == null
            || command.getCouponId() <= 0) {
            throw new InvalidCouponIdException();
        }

        Coupon coupon = couponRepository.getCoupon(command.getCouponId())
            .orElseThrow(CouponNotFoundException::new);

        if (!coupon.canIssue()) {
            throw new InsufficientCouponQuantityException();
        }

        return coupon;
    }

}
