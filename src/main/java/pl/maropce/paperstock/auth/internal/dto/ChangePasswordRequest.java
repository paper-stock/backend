package pl.maropce.paperstock.auth.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 70, message = "Password must have 8-70 characters")
        String oldPassword,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 70, message = "Password must have 8-70 characters")
        String newPassword
) {
}
