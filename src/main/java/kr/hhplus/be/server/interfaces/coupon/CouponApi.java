package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(
    name = "COUPON API",
    description = "쿠폰 관련 API입니다.")
public interface CouponApi {

    @Operation(
        summary = "선착순 쿠폰 발급",
        description = "선착순 쿠폰 발급을 요청합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "쿠폰 발급 성공",
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
    ResponseEntity<ApiResult<CouponResponse.Coupon>> issueCoupon(
        @Parameter(
            description = "쿠폰 식별자",
            example = "1"
        )
        @PathVariable
        @Min(value = 1, message = "쿠폰 식별자가 유효하지 않습니다.") long id,
        @Valid
        CouponRequest.Issue couponIssueRequest);
}
