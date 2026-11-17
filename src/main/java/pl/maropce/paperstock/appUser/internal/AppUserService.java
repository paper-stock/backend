package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserRequest;
import pl.maropce.paperstock.appUser.internal.dto.AppUserMapper;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;


    @Transactional
    public AppUserDTO create(CreateAppUserRequest createAppUserRequest) {

        AppUser appUser = AppUser.builder()
                .displayName(createAppUserRequest.displayName())
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
}
