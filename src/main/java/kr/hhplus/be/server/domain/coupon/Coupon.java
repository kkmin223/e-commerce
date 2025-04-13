package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class Coupon {
    private Long id;
    private String title;
    private Integer initialQuantity;
    private Integer remainingQuantity;

    public abstract Integer apply(Integer amount);

    public abstract CouponType getCouponType();

    public abstract String getDiscountLabel();

    protected Coupon(String title, Integer initialQuantity) {
        this.title = title;
        this.initialQuantity = initialQuantity;
        this.remainingQuantity = initialQuantity;
    }

    public Boolean canIssue() {
        return 0 < remainingQuantity;
    }

    public void decreaseRemainingQuantity() {
        if (remainingQuantity == 0) {
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_COUPON_QUANTITY);
        }

        this.remainingQuantity--;
    }
}
