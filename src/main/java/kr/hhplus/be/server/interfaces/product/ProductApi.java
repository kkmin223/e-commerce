package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
    name = "PRODUCT API",
    description = "상품 관련 API입니다.")
public interface ProductApi {

    @Operation(
        summary = "상품 리스트 조회",
        description = "등록된 상품 리스트를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "상품 리스트 조회 성공",
            useReturnTypeSchema = true
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                mediaType = "application/json"
            )
        )
    })
    ResponseEntity<ApiResult<List<ProductResponse.Product>>> listProduct();

    @Operation(
        summary = "상품 단건 조회",
        description = "특정 상품을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "상품 단건 조회 성공",
            useReturnTypeSchema = true
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Request 유효성 에러",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                mediaType = "application/json"
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 리소스 접근",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                mediaType = "application/json"
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                mediaType = "application/json"
            )
        )
    })
    ResponseEntity<ApiResult<ProductResponse.Product>> getProduct(
        @Parameter(
            description = "상품 고유 ID",
            example = "1"
        )
        @PathVariable @Min(value = 1, message = "상품 식별자가 유효하지 않습니다.") long id);

    @Operation(
        summary = "인기 상품 리스트 조회",
        description = "판매량 상위 5개의 상품을 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "인기 상품 리스트 조회",
            useReturnTypeSchema = true
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                schema = @Schema(implementation = ErrorResult.class),
                mediaType = "application/json"
            )
        )
    })
    ResponseEntity<ApiResult<List<ProductResponse.Product>>> listPopularProduct();
}
