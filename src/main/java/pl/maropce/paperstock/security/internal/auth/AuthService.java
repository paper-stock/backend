package pl.maropce.paperstock.security.internal.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserCommand;
import pl.maropce.paperstock.security.AppUserDetails;
import pl.maropce.paperstock.security.internal.auth.dto.*;
import pl.maropce.paperstock.security.internal.auth.jwt.JwtUtil;
import pl.maropce.paperstock.security.internal.auth.jwt.refreshToken.RefreshTokenService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AppUserApi appUserApi;

    public AppUserDTO register(@Valid RegisterUserRequest registerUserRequest) {
        CreateAppUserCommand createAppUserCommand = new CreateAppUserCommand(
                registerUserRequest.email(),
                registerUserRequest.password(),
                registerUserRequest.displayName()
        );
        return appUserApi.create(createAppUserCommand);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                ));

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AppUserDetails appUserDetails)) {
            throw new IllegalStateException("Authentication principal is not an instance of AppUserDetails");
        }

        String accessToken = jwtUtil.generateToken(appUserDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(appUserDetails);
        return new LoginResponse(accessToken, refreshToken);
    }

    public AppUserDTO whoami(AppUserDetails appUserDetails) {
        return appUserApi.findAppUserById(appUserDetails.getAppUserId());
    }

    public void logout(LogoutRequest logoutRequest) {
        refreshTokenService.delete(logoutRequest.refreshToken());
    }

    @Transactional
    public void changePassword(AppUserDetails appUserDetails, ChangePasswordRequest changePasswordRequest) {
        appUserApi.changePassword(
                appUserDetails,
                changePasswordRequest.oldPassword(),
                changePasswordRequest.newPassword()
        );

        refreshTokenService.deleteAllByUsername(appUserDetails.getUsername());

        log.info("Password changed successfully for user: {}", appUserDetails.getUsername());
    }
}