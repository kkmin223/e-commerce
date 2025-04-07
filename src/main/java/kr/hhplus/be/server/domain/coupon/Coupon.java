package kr.hhplus.be.server.domain.coupon;

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

    protected Coupon(String title, Integer initialQuantity) {
        this.title = title;
        this.initialQuantity = initialQuantity;
        this.remainingQuantity = initialQuantity;
    }
}
