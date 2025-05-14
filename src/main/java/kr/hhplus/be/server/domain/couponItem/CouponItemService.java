package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponItemService {

    private final CouponItemRepository couponItemRepository;

    public CouponItem getCouponItem(CouponItemCommand.Get command) {
        return couponItemRepository.findById(command.getCouponItemId())
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.COUPON_NOT_FOUND));
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

    public List<CouponItem> findByUser(CouponItemCommand.FindByUser command) {
        if (command.getUser() == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        }

        return couponItemRepository.findByUser(command.getUser());
    }

}
