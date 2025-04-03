package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.ProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(
    name = "PRODUCT API",
    description = "상품 관련 API입니다.")
@RequestMapping("/products")
public interface ProductApi {

    @Operation(
        summary = "상품 리스트 조회",
        description = "등록된 상품 리스트를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "상품 리스트 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                mediaType = "application/json"
            )
        )
    })
    @GetMapping()
    ResponseEntity<List<ProductResponseDto>> listProduct();

    @Operation(
        summary = "상품 단건 조회",
        description = "특정 상품을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "상품 단건 조회 성공",
            content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request 유효성 에러",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                mediaType = "application/json"
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 리소스 접근",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                mediaType = "application/json"
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                mediaType = "application/json"
            )
        )
    })
    @GetMapping("/{id}")
    ResponseEntity<ProductResponseDto> getProduct(
        @Parameter(
            description = "상품 고유 ID",
            example = "1"
        )
        @PathVariable @Min(value = 1, message = "상품 식별자가 유효하지 않습니다.") Long id);

    @Operation(
        summary = "인기 상품 리스트 조회",
        description = "판매량 상위 5개의 상품을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인기 상품 리스트 조회",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                mediaType = "application/json"
            )
        )
    })
    @GetMapping("/popular")
    ResponseEntity<List<ProductResponseDto>> listPopularProduct();
}
