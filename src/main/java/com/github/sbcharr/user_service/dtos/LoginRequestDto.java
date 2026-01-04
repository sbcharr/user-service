package com.github.sbcharr.user_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "Email required")
        @Email(message = "Valid email required")
        String email,
        @NotBlank(message = "Password required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password) {}

