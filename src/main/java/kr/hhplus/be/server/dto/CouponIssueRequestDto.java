package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "선착순 쿠폰 발급 요청 DTO"
)
public class CouponIssueRequestDto {

    @Schema(
        description = "사용자 식별자",
        example = "1"
    )
    @Min(value = 1, message = "사용자 식별자가 유효하지 않습니다.")
    private Long userId;
}
