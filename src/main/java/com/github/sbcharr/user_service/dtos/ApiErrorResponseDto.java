package com.github.sbcharr.user_service.dtos;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiErrorResponseDto {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponseDto(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
