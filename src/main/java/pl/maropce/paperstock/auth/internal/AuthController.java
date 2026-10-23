package pl.maropce.paperstock.auth.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.auth.internal.dto.*;
import pl.maropce.paperstock.jwt.TokenResponse;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<AppUserAccountDTO> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(
                authService.register(registerUserRequest)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(
                authService.login(loginRequest)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {

        return ResponseEntity.ok(
                authService.refreshAccessToken(refreshRequest.refreshToken())
        );
    }

    @GetMapping("/whoami")
    public AppUserAccountDTO whoami(
            @AuthenticationPrincipal AppUserDetails appUserDetails
    ) {
        return authService.whoami(appUserDetails);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequest logoutRequest

    ) {
        authService.logout(logoutRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        authService.changePassword(appUserDetails, changePasswordRequest);
        return ResponseEntity.noContent().build();
    }
}
