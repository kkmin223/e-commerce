package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponItemService {

    private final CouponItemRepository couponItemRepository;

    public CouponItem getCouponItem(CouponItemCommand.Get command) {
        return couponItemRepository.getCouponItem(command.getCouponItemId());
    }

    public CouponItem issueCouponItem(CouponItemCommand.Issue command) {
        if (command.getUser() == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        if (command.getCoupon() == null) {
            throw new BusinessLogicException(ErrorCode.COUPON_NOT_FOUND);
        }

        CouponItem issuedCouponItem = CouponItem.issue(command.getUser(), command.getCoupon());
        return couponItemRepository.save(issuedCouponItem);
    }

}
