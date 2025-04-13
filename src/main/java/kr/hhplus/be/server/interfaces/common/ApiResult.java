package kr.hhplus.be.server.interfaces.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "API 공통 응답 DTO"
)
public class ApiResult<T> {
    @Schema(
        description = "응답 코드",
        example = "200"
    )
    private String code;
    @Schema(
        description = "응답 메세지",
        example = "응답 성공"
    )
    private String message;
    private T data;

    public static <T> ApiResult<T> of(Code code, T data) {
        return new ApiResult<>(code.getCode(), code.getMessage(), data);
    }

}
