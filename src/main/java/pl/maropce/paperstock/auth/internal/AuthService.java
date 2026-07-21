package pl.maropce.paperstock.auth.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserCommand;
import pl.maropce.paperstock.auth.AppUserDetails;
import pl.maropce.paperstock.auth.AppUserRegisteredEvent;
import pl.maropce.paperstock.auth.internal.dto.LoginRequest;
import pl.maropce.paperstock.auth.internal.dto.LoginResponse;
import pl.maropce.paperstock.auth.internal.dto.RegisterUserRequest;
import pl.maropce.paperstock.auth.internal.jwt.JwtUtil;
import pl.maropce.paperstock.auth.internal.jwt.refreshToken.RefreshTokenService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher publisher;
    private final AppUserApi appUserApi;

    public AppUserDTO register(@Valid RegisterUserRequest registerUserRequest) {
        CreateAppUserCommand createAppUserCommand = new CreateAppUserCommand(
                registerUserRequest.email(),
                registerUserRequest.password(),
                registerUserRequest.displayName()
        );
        AppUserDTO appUserDTO = appUserApi.create(createAppUserCommand);

        publisher.publishEvent(new AppUserRegisteredEvent(appUserDTO));

        return appUserDTO;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AppUserDetails userDetails)) {
            throw new IllegalStateException("Authentication principal is not an instance of AppUserDetails");
        }

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails.getUsername());
        return new LoginResponse(accessToken, refreshToken);
    }

    public AppUserDTO whoami(UUID appUserId) {
        return appUserApi.findAppUserById(appUserId);
    }
}
