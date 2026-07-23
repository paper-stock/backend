package pl.maropce.paperstock.security.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserCommand;
import pl.maropce.paperstock.security.AppUserDetails;
import pl.maropce.paperstock.security.auth.dto.LoginRequest;
import pl.maropce.paperstock.security.auth.dto.LoginResponse;
import pl.maropce.paperstock.security.auth.dto.RegisterUserRequest;
import pl.maropce.paperstock.security.auth.jwt.JwtUtil;
import pl.maropce.paperstock.security.auth.jwt.refreshToken.RefreshTokenService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
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
