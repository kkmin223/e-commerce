package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.common.exceptions.InsufficientStockException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidIncreaseQuantityException;
import kr.hhplus.be.server.interfaces.common.exceptions.InvalidReduceQuantityException;
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
            throw new InvalidReduceQuantityException();
        }

        this.quantity -= quantity;

        if (this.quantity < 0) {
            throw new InsufficientStockException();
        }
    }

    public void increaseQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new InvalidIncreaseQuantityException();
        }

        this.quantity += quantity;
    }
}
