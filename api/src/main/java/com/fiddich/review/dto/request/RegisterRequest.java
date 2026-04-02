package com.fiddich.review.dto.request;

import com.fiddich.review.user.Platform;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull Platform platform
) {
}
