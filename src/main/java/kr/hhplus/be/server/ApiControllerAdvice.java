package kr.hhplus.be.server;

import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.interfaces.common.ErrorResult;
import kr.hhplus.be.server.interfaces.common.exceptions.BusinessLogicException;
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
    public ResponseEntity<ErrorResult> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        BindingResult bindingResult = ex.getBindingResult();

        // field : message 형식으로 변환
        String errorMessage = bindingResult.getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity.status(400).body(new ErrorResult("INVALID_INPUT_ERROR", errorMessage));
    }

    // Request 유효성 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(400).body(new ErrorResult("INVALID_INPUT_ERROR", ex.getMessage()));
    }

    // Custom Exception 에러
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResult> handleCustomException(BusinessLogicException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(
            new ErrorResult(ex.getCode(), ex.getMessage())
        );
    }

    // 서버 에러
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(500).body(new ErrorResult("INTERNAL_SERVER_ERROR", "에러가 발생했습니다."));
    }
}
