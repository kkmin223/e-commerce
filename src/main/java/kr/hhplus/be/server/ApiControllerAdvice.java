package kr.hhplus.be.server;

import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.interfaces.common.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApiControllerAdvice {

    // Request 유효성 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        // field : message 형식으로 변환
        String errorMessage = bindingResult.getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity.status(400).body(new ErrorResult("400", errorMessage));
    }

    // Request 유효성 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.status(400).body(new ErrorResult("400", e.getMessage()));
    }


    // 존재하지 않는 리소스 접근
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> handleRuntimeException(IllegalArgumentException e) {
        return ResponseEntity.status(500).body(new ErrorResult("404", e.getMessage()));
    }

    // 서버 에러
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(500).body(new ErrorResult("500", "에러가 발생했습니다."));
    }
}
