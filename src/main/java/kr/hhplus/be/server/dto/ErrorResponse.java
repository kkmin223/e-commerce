package kr.hhplus.be.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "에러 응답 DTO"
)
public class ErrorResponse {
    @Schema(description = "에러 코드")
    String code;
    @Schema(description = "에러 메세지")
    String message;
}
