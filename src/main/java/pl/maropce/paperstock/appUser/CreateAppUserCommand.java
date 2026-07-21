package pl.maropce.paperstock.appUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppUserCommand(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must have 8-100 characters")
        String password,

        @NotBlank(message = "Display name is required")
        @Size(min = 3, max = 20, message = "Display name must have 3-20 characters")
        String displayName

) {}
