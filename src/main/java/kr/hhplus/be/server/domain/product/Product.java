package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.ErrorCode;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Integer quantity;
    private Integer price;

    public static Product of(Long id, String name, Integer quantity, Integer price) {
        return new Product(id, name, quantity, price);
    }

    public boolean canOrder(Integer quantity) {
        return quantity <= this.quantity;
    }

    public void reduceQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_REDUCE_QUANTITY);
        }

        this.quantity -= quantity;

        if (this.quantity < 0) {
            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

    public void increaseQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessLogicException(ErrorCode.INVALID_INCREASE_QUANTITY);
        }

        this.quantity += quantity;
    }
}
