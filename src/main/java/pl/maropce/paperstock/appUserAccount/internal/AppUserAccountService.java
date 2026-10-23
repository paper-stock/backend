package pl.maropce.paperstock.appUserAccount.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;
import pl.maropce.paperstock.appUserAccount.Role;
import pl.maropce.paperstock.appUserAccount.internal.dto.AppUserAccountMapper;
import pl.maropce.paperstock.appUserAccount.internal.dto.CreateNewAccountRequest;
import pl.maropce.paperstock.appUserAccount.internal.exception.AppUserAccountNotFoundException;
import pl.maropce.paperstock.appUserAccount.internal.exception.InvalidOldPasswordException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserAccountService {

    private final AppUserAccountRepository appUserAccountRepository;
    private final AppUserAccountMapper appUserAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) {
        return appUserAccountRepository.existsByEmail(email);
    }

    public AppUserAccountDTO createNewAccount(CreateNewAccountRequest request) {

        AppUserAccount appUserAccount = AppUserAccount.builder()
                .appUserId(request.appUserId())
                .email(request.email())
                .password(request.password())
                .role(Role.USER)
                .enabled(true)
                .build();

        AppUserAccount save = appUserAccountRepository.save(appUserAccount);
        return appUserAccountMapper.toDTO(save);
    }

    public AppUserAccountDTO findByEmail(String email) {
        AppUserAccount appUserAccount = appUserAccountRepository.findByEmail(email)
                .orElseThrow(() -> new AppUserAccountNotFoundException(email));

        return appUserAccountMapper.toDTO(appUserAccount);
    }

    public void changePassword(UUID appUserAccountId, String oldPassword, String newPassword) {
        AppUserAccount appUserAccount = appUserAccountRepository.findById(appUserAccountId)
                .orElseThrow(() -> new AppUserAccountNotFoundException(appUserAccountId));

        if (!passwordEncoder.matches(oldPassword, appUserAccount.getPassword())) {
            throw new InvalidOldPasswordException(appUserAccount.getId());
        }

        appUserAccount.setPassword(passwordEncoder.encode(newPassword));
        appUserAccountRepository.save(appUserAccount);
    }
}
