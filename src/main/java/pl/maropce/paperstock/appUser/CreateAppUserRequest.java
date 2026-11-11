package pl.maropce.paperstock.appUser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppUserRequest(

        @NotBlank(message = "Display name is required")
        @Size(min = 3, max = 20, message = "Display name must have 3-20 characters")
        String displayName
) {
}
