package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/products")
public class ProductController implements ProductApi {
    @Override
    @GetMapping()
    public ResponseEntity<ApiResult<List<ProductResponse.Product>>> listProduct() {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.LIST_PRODUCT, List.of(new ProductResponse.Product(1L, "상품", 10000, 10))));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ProductResponse.Product>> getProduct(long id) {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.GET_PRODUCT, new ProductResponse.Product(1L, "상품", 10000, 10)));
    }

    @Override
    @GetMapping("/popular")
    public ResponseEntity<ApiResult<List<ProductResponse.Product>>> listPopularProduct() {
        return ResponseEntity.ok(ApiResult.of(SuccessCode.LIST_POPULAR_PRODUCT, List.of(new ProductResponse.Product(1L, "상품", 10000, 10))));
    }
}
