package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.*;
import pl.maropce.paperstock.appUser.internal.dto.AppUserMapper;
import pl.maropce.paperstock.appUser.internal.exception.InvalidOldPasswordException;
import pl.maropce.paperstock.appUser.internal.exception.UserAlreadyExistsException;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserAccountRepository appUserAccountRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public AppUserDTO create(CreateAppUserRequest createAppUserRequest) {

        if (appUserAccountRepository.existsByEmail(createAppUserRequest.email())) {
            throw new UserAlreadyExistsException(createAppUserRequest.email());
        }

        AppUser appUser = AppUser.builder()
                .displayName(createAppUserRequest.displayName())
                .build();
        AppUserAccount appUserAccount = AppUserAccount.builder()
                .email(createAppUserRequest.email())
                .password(
                        passwordEncoder.encode(
                                createAppUserRequest.password()
                        )
                )
                .role(Role.USER)
                .appUser(appUser)
                .isEnabled(true)
                .build();

        AppUserAccount userAccount = appUserAccountRepository.save(appUserAccount);

        applicationEventPublisher.publishEvent(new AppUserCreatedEvent(userAccount.getAppUser().getId()));

        return appUserMapper.toDTO(userAccount.getAppUser());
    }

    @Transactional
    public AppUserAuthenticationData getAppUserAuthData(String email) {
        AppUserAccount appUserAccount = appUserAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return AppUserAuthenticationData.builder()
                .appUserId(appUserAccount.getAppUser().getId())
                .email(appUserAccount.getEmail())
                .password(appUserAccount.getPassword())
                .role(appUserAccount.getRole())
                .isEnabled(appUserAccount.isEnabled())
                .build();
    }

    @Transactional
    public AppUserDTO findAppUserById(UUID appUserId) {
        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));
        return appUserMapper.toDTO(appUser);
    }

    @Transactional
    public void changePassword(UUID appUserId, String oldPassword, String newPassword) {
        AppUserAccount appUserAccount = appUserAccountRepository.findByAppUser_Id(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));

        if (!passwordEncoder.matches(oldPassword, appUserAccount.getPassword())) {
            throw new InvalidOldPasswordException(appUserId);
        }

        appUserAccount.setPassword(passwordEncoder.encode(newPassword));
        appUserAccountRepository.save(appUserAccount);
    }
}
