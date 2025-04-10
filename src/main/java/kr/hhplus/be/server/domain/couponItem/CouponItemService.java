package kr.hhplus.be.server.domain.couponItem;

import kr.hhplus.be.server.interfaces.common.exceptions.CouponNotFoundException;
import kr.hhplus.be.server.interfaces.common.exceptions.UserNotFoundException;
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
            throw new UserNotFoundException();
        }

        if (command.getCoupon() == null) {
            throw new CouponNotFoundException();
        }

        CouponItem issuedCouponItem = CouponItem.issue(command.getUser(), command.getCoupon());
        return couponItemRepository.save(issuedCouponItem);
    }

}
