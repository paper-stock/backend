package pl.maropce.paperstock.auth.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.auth.AppUserDetails;
import pl.maropce.paperstock.auth.internal.dto.LoginRequest;
import pl.maropce.paperstock.auth.internal.dto.LoginResponse;
import pl.maropce.paperstock.auth.internal.dto.RefreshRequest;
import pl.maropce.paperstock.auth.internal.dto.RegisterUserRequest;
import pl.maropce.paperstock.auth.internal.jwt.refreshToken.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    ResponseEntity<AppUserDTO> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(
                authService.register(registerUserRequest)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(
                authService.login(loginRequest)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(
                refreshTokenService.refreshAccessToken(refreshRequest.refreshToken())
        );
    }

    @GetMapping("/whoami")
    public AppUserDTO whoami(
            @AuthenticationPrincipal AppUserDetails appUserDetails
    ) {
        return authService.whoami(appUserDetails.getAppUserId());
    }
}
