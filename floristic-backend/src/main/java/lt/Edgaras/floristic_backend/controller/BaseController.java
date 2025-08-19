package lt.Edgaras.floristic_backend.controller;

import lt.Edgaras.floristic_backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    //status 2**
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(data, message, true));
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(new ApiResponse<>(null, message, true));
    }

    protected <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(data, message, true));
    }

    protected <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(null, null, true));
    }

    //status 4**
    protected <T> ResponseEntity<ApiResponse<T>> notAuthenticated(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(null, message, false));
    }

    protected <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(null, message, false));
    }

    protected <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(null, message, false));
    }

    protected <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(null, message, false));
    }

    protected <T> ResponseEntity<ApiResponse<T>> toManyRequests(String message) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ApiResponse<>(null, message, false));
    }

    //status 5**
    protected <T> ResponseEntity<ApiResponse<T>> badRequest(T data, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(data, message, false));
    }

}
