package pl.maropce.paperstock.security.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.security.AppUserDetails;
import pl.maropce.paperstock.security.auth.dto.LoginRequest;
import pl.maropce.paperstock.security.auth.dto.LoginResponse;
import pl.maropce.paperstock.security.auth.dto.RefreshRequest;
import pl.maropce.paperstock.security.auth.dto.RegisterUserRequest;
import pl.maropce.paperstock.security.auth.jwt.refreshToken.RefreshTokenService;

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
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
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
