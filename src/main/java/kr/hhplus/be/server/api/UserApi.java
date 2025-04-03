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
import kr.hhplus.be.server.dto.AmountResponseDto;
import kr.hhplus.be.server.dto.ChargeRequestDto;
import kr.hhplus.be.server.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
    name = "USER API",
    description = "사용자 관련 API입니다.")
@RequestMapping("/user")
public interface UserApi {

    @Operation(
        summary = "사용자 잔액 조회",
        description = "특정 사용자의 잔액을 조회힙나다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "잔액 조회 성공",
            content = @Content(schema = @Schema(implementation = AmountResponseDto.class))
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
    @GetMapping("/{id}/amount")
    ResponseEntity<AmountResponseDto> getUserAmount(
        @Parameter(
            description = "사용자 고유 ID",
            example = "1"
        )
        @PathVariable @Min(1) Long id);

    @Operation(
        summary = "사용자 잔액 충전",
        description = "사용자 잔액을 충전하고 잔액을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "잔액 충전 성공",
            content = @Content(
                schema = @Schema(implementation = AmountResponseDto.class),
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
    @PostMapping("/{id}/amount/charge")
    ResponseEntity<AmountResponseDto> chargeUserAmount(
        @Parameter(
            description = "사용자 고유 ID",
            example = "1"
        )
        @PathVariable @Min(1) Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChargeRequestDto.class)
            )
        )
        @Valid ChargeRequestDto chargeRequestDto);
}



