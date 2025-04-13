package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseEntity;
import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "coupon_type")
public abstract class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer initialQuantity;

    @Column(nullable = false)
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
