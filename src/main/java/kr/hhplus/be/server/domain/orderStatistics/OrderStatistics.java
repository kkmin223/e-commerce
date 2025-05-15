package kr.hhplus.be.server.domain.orderStatistics;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class OrderStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate statisticDate;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer soldQuantity;

    private OrderStatistics(LocalDate statisticDate, Product product, Integer soldQuantity) {
        this.statisticDate = statisticDate;
        this.productId = product.getId();
        this.soldQuantity = soldQuantity;
    }

    private OrderStatistics(LocalDate statisticDate, Long productId, Integer soldQuantity) {
        this.statisticDate = statisticDate;
        this.productId = productId;
        this.soldQuantity = soldQuantity;
    }

    public static OrderStatistics of(LocalDate statisticDate, Product product, Integer soldQuantity) {
        return new OrderStatistics(statisticDate, product, soldQuantity);
    }

    public static OrderStatistics of(LocalDate statisticDate, Long productId, Integer soldQuantity) {
        return new OrderStatistics(statisticDate, productId, soldQuantity);
    }
}
