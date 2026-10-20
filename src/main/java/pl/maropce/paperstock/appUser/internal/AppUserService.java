package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserAuthenticationData;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserRequest;
import pl.maropce.paperstock.appUser.Role;
import pl.maropce.paperstock.appUser.internal.dto.AppUserMapper;
import pl.maropce.paperstock.appUser.internal.exception.InvalidOldPasswordException;
import pl.maropce.paperstock.appUser.internal.exception.NotEnoughFundsException;
import pl.maropce.paperstock.appUser.internal.exception.UserAlreadyExistsException;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserAccountRepository appUserAccountRepository;
    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Transactional
    public AppUserDTO create(CreateAppUserRequest createAppUserRequest) {

        if (appUserAccountRepository.existsByEmail(createAppUserRequest.email())) {
            throw new UserAlreadyExistsException(createAppUserRequest.email());
        }

        AppUser appUser = AppUser.builder()
                .displayName(createAppUserRequest.displayName())
                .funds(BigDecimal.ZERO)
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

    public AppUserDTO addFunds(UUID appUserId, AddFundsRequest request) {
        log.debug("Adding {} amount of funds for user {}", request.amount(), appUserId);

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));

        BigDecimal funds = appUser.getFunds();

        BigDecimal newAmount = funds.add(request.amount());

        appUser.setFunds(newAmount);

        return appUserMapper.toDTO(
                appUserRepository.save(appUser));
    }

    //TODO do sprawdzenia
    public void payFunds(UUID appUserId, BigDecimal amount) {
        log.debug("Paying {} amount of funds for user {}", amount, appUserId);

        assert amount != null && amount.signum() > 0 : "Amount must be positive";

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));

        if (appUser.getFunds().compareTo(amount) < 0) {
            throw new NotEnoughFundsException();
        }

        appUser.setFunds(
                appUser.getFunds().subtract(amount)
        );

        appUserRepository.save(appUser);
    }
}
