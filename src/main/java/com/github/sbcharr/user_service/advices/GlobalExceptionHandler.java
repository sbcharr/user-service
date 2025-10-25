package com.github.sbcharr.user_service.advices;

import com.github.sbcharr.user_service.dtos.ApiErrorResponse;
import com.github.sbcharr.user_service.exceptions.InvalidTokenException;
import com.github.sbcharr.user_service.exceptions.PasswordMismatchException;
import com.github.sbcharr.user_service.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlredyExists(UserAlreadyExistsException ex,
                                                                   WebRequest request) {

        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({InvalidTokenException.class, PasswordMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception ex,
                                                                       WebRequest request) {

        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

}
