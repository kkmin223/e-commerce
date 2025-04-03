package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.api.ProductApi;
import kr.hhplus.be.server.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class ProductController implements ProductApi {
    @Override
    public ResponseEntity<List<ProductResponseDto>> listProduct() {
        return ResponseEntity.ok(List.of(new ProductResponseDto(1L, "상품", 10000, 10)));
    }

    @Override
    public ResponseEntity<ProductResponseDto> getProduct(Long id) {
        return ResponseEntity.ok(new ProductResponseDto(1L, "상품", 10000, 10));
    }

    @Override
    public ResponseEntity<List<ProductResponseDto>> listPopularProduct() {
        return ResponseEntity.ok(List.of(new ProductResponseDto(1L, "상품", 10000, 10)));
    }
}
