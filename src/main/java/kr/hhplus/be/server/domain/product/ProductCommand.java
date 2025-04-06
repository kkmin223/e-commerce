package kr.hhplus.be.server.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductCommand {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Long productId;
    }
}
