package kr.hhplus.be.server.domain.couponItem;

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
        return CouponItem.issue(command.getUser(), command.getCoupon());
    }

}
