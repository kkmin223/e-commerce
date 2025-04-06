package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductCriteria {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Long productId;

        public ProductCommand.Get toCommand() {
            return new ProductCommand.Get(this.productId);
        }
    }
}
