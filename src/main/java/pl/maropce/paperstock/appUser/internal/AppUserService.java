package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserAuthenticationData;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.Role;
import pl.maropce.paperstock.appUser.internal.dto.AppUserMapper;
import pl.maropce.paperstock.appUser.CreateAppUserCommand;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserAccountRepository appUserAccountRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Transactional
    public AppUserDTO create(CreateAppUserCommand createAppUserCommand) {
        AppUser appUser = AppUser.builder().displayName(createAppUserCommand.displayName()).build();
        AppUserAccount appUserAccount = AppUserAccount.builder()
                .email(createAppUserCommand.email())
                .password(
                        passwordEncoder.encode(
                                createAppUserCommand.password()
                        )
                )
                .roles(Role.USER)
                .appUser(appUser)
                .isEnabled(true)
                .build();

        AppUserAccount userAccount = appUserAccountRepository.save(appUserAccount);

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
                .role(appUserAccount.getRoles())
                .isEnabled(appUserAccount.isEnabled())
                .build();
    }

    public AppUserDTO findAppUserById(UUID appUserId) {
        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));
        return appUserMapper.toDTO(appUser);
    }
}
