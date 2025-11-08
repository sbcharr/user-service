package com.github.sbcharr.user_service.advices;

import com.github.sbcharr.user_service.dtos.ApiErrorResponseDto;
import com.github.sbcharr.user_service.exceptions.InvalidCredentialsException;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.PasswordMismatchException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiErrorResponseDto> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ApiErrorResponseDto response = new ApiErrorResponseDto(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
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
    public ResponseEntity<ApiErrorResponseDto> handleBadRequestExceptions(Exception ex,
                                                                          WebRequest request) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleAllUncaught(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {} - {}", ex.getMessage(), request.getDescription(false), ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
