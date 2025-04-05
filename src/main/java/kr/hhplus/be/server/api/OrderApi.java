package kr.hhplus.be.server.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.dto.ErrorResponse;
import kr.hhplus.be.server.dto.OrderRequestDto;
import kr.hhplus.be.server.dto.OrderResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
    name = "ORDER API",
    description = "주문 관련 API입니다.")
@RequestMapping("/orders")
public interface OrderApi {

    @Operation(
        summary = "상품 주문",
        description = "상품을 주문하고 결제합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "주문 성공",
            content = @Content(
                schema = @Schema(implementation = OrderResponseDto.class),
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
    @PostMapping()
    ResponseEntity<OrderResponseDto> order(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OrderRequestDto.class)
            )
        )
        @Valid
        OrderRequestDto couponIssueRequestDto
    );
}
