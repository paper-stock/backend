package pl.maropce.paperstock.security;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUserAccount.AppUserAccountApi;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;

@Service
@RequiredArgsConstructor
public class AppUserDetailServiceImpl implements UserDetailsService {

    private final AppUserAccountApi appUserAccountApi;

    @NullMarked
    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            AppUserAccountDTO appUserAccount = appUserAccountApi.findByEmail(username);

            return AppUserDetails.builder()
                    .id(appUserAccount.id())
                    .appUserId(appUserAccount.appUserId())
                    .username(appUserAccount.email())
                    .password(appUserAccount.password())
                    .isEnabled(appUserAccount.enabled())
                    .role(appUserAccount.role())
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with: " + username);
        }
    }
}
