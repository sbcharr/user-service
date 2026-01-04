package com.github.sbcharr.user_service.advices;

import com.github.sbcharr.user_service.dtos.ApiErrorResponseDto;
import com.github.sbcharr.user_service.exceptions.InvalidCredentialsException;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.PasswordMismatchException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<ApiErrorResponseDto> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        String cleanPath = request.getDescription(false).replace("uri=", "");
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                cleanPath
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                                       WebRequest request) {
        log.info("UserAlreadyExists: {} - {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponseDto> handleInvalidToken(Exception ex, WebRequest request) {
        log.warn("InvalidToken: {} - {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleInvalidCredentials(InvalidCredentialsException ex,
                                                                        WebRequest request) {
        log.warn("InvalidCredentials: {} - {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiErrorResponseDto> handlePasswordMismatch(PasswordMismatchException ex,
                                                                      WebRequest request) {
        log.info("PasswordMismatch: {} - {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    // @Valid DTO validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {} - {}", errors, request.getDescription(false));
        return buildErrorResponse(new IllegalArgumentException(errors),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleAllUncaught(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {} - {}", ex.getMessage(), request.getDescription(false), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
