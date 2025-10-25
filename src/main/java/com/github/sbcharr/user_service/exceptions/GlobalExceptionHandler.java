package com.github.sbcharr.user_service.exceptions;

import com.github.sbcharr.user_service.dtos.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlredyExists(UserAlreadyExistsException ex,
                                                                   WebRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
