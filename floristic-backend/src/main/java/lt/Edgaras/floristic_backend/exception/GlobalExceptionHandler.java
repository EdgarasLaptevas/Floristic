package lt.Edgaras.floristic_backend.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import lt.Edgaras.floristic_backend.controller.BaseController;
import lt.Edgaras.floristic_backend.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach((violation) -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return badRequest(errors, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return badRequest(errors, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return badRequest(null, ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
        return badRequest(null, ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse<Void>> handleJsonProcessingException(JsonProcessingException ex) {
        return badRequest(null, ex.getMessage());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ApiResponse<>(null, ex.getMessage(), false));
    }
}
