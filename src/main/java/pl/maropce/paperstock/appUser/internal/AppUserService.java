package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserRequest;
import pl.maropce.paperstock.appUser.internal.dto.AppUserMapper;
import pl.maropce.paperstock.appUser.internal.exception.NotEnoughFundsException;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;


    public AppUserDTO create(CreateAppUserRequest createAppUserRequest) {

        AppUser appUser = AppUser.builder()
                .displayName(createAppUserRequest.displayName())
                .funds(BigDecimal.ZERO)
                .build();

        AppUser savedAppUser = appUserRepository.save(appUser);

        return appUserMapper.toDTO(savedAppUser);
    }

    @Transactional
    public AppUserDTO findAppUserById(UUID appUserId) {
        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new UserNotFoundException(appUserId));
        return appUserMapper.toDTO(appUser);
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
