package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.hhplus.be.server.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
    name = "COUPON API",
    description = "쿠폰 관련 API입니다.")
@RequestMapping("/coupons")
public interface CouponApi {

    @Operation(
        summary = "선착순 쿠폰 발급",
        description = "선착순 쿠폰 발급을 요청합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "쿠폰 발급 성공",
            content = @Content(
                schema = @Schema(implementation = CouponResponseDto.class),
                mediaType = "application/json"
            )
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
    @PostMapping("/{id}/issue")
    ResponseEntity<CouponResponseDto> issueCoupon(
        @Parameter(
            description = "쿠폰 식별자",
            example = "1"
        )
        @PathVariable
        @Min(value = 1, message = "쿠폰 식별자가 유효하지 않습니다.") long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CouponIssueRequestDto.class)
            )
        )
        @Valid
        CouponIssueRequestDto couponIssueRequestDto);
}
