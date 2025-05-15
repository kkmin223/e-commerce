package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/products")
public class ProductController implements ProductApi {

    private final ProductService productService;
    private final ProductFacade productFacade;

    @Override
    @GetMapping()
    public ResponseEntity<ApiResult<List<ProductResponse.Product>>> listProduct() {
        List<Product> products = productService.listProducts();

        return ResponseEntity.ok(
            ApiResult.of(
                SuccessCode.LIST_PRODUCT,
                products.stream().map(product -> ProductResponse.Product.of(product.getId(), product.getName(), product.getPrice(), product.getQuantity())).toList()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ProductResponse.Product>> getProduct(long id) {
        Product product = productService.getProduct(ProductCommand.Get.of(id));


        return ResponseEntity.ok(
            ApiResult.of(
                SuccessCode.GET_PRODUCT,
                ProductResponse.Product.of(product.getId(), product.getName(), product.getPrice(), product.getQuantity())));
    }

    @Override
    @GetMapping("/popular")
    public ResponseEntity<ApiResult<List<ProductResponse.ProductRanking>>> listPopularProduct() {
        LocalDate today = LocalDate.now();
        List<ProductInfo.ProductRanking> topProduct = productService.getTopProduct(ProductCommand.GetTopProduct.of(today.minusDays(3), today, 5));

        List<ProductResponse.ProductRanking> result = topProduct
            .stream()
            .map(product -> ProductResponse.ProductRanking.of(product.getRanking(), product.getProductId(), product.getProductName(), product.getSoldQuantity()))
            .toList();

        return ResponseEntity.ok(ApiResult.of(SuccessCode.LIST_POPULAR_PRODUCT, result));
    }
}
