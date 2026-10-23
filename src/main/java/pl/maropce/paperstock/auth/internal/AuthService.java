package pl.maropce.paperstock.auth.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUserAccount.AppUserAccountApi;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;
import pl.maropce.paperstock.auth.internal.dto.*;
import pl.maropce.paperstock.auth.internal.exception.AccountAlreadyExistsException;
import pl.maropce.paperstock.jwt.JwtApi;
import pl.maropce.paperstock.jwt.TokenResponse;
import pl.maropce.paperstock.security.AppUserDetails;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtApi jwtApi;
    private final AppUserAccountApi appUserAccountApi;
    private final AppUserApi appUserApi;

    @Transactional
    public AppUserAccountDTO register(RegisterUserRequest request) {

        if (appUserAccountApi.existsByEmail(request.email())) {
            throw new AccountAlreadyExistsException(request.email());
        }

        AppUserDTO appUserDTO = appUserApi.create(request.displayName());

        return appUserAccountApi.createNewAccount(
                request.email(),
                passwordEncoder.encode(request.password()),
                appUserDTO.id()
        );

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

        String accessToken = jwtApi.generateToken(appUserDetails);
        String refreshToken = jwtApi.generateRefreshToken(appUserDetails);
        return new LoginResponse(accessToken, refreshToken);
    }

    public AppUserAccountDTO whoami(AppUserDetails appUserDetails) {
        return appUserAccountApi.findByEmail(appUserDetails.getUsername());
    }

    public void logout(LogoutRequest logoutRequest) {
        jwtApi.deleteRefreshToken(
                logoutRequest.refreshToken()
        );
    }

    @Transactional
    public void changePassword(AppUserDetails appUserDetails, ChangePasswordRequest request) {

        appUserAccountApi.changePassword(appUserDetails.getId(), request.oldPassword(), request.newPassword());

        jwtApi.deleteAllRefreshTokensByUsername(appUserDetails.getUsername());

        log.info("Password changed successfully for account: {}", appUserDetails.getId());
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        return jwtApi.refreshAccessToken(refreshToken);
    }
}