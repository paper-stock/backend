package pl.maropce.paperstock.auth.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserAuthenticationData;
import pl.maropce.paperstock.auth.AppUserDetails;

@Service
@RequiredArgsConstructor
public class AppUserDetailsServiceImpl implements UserDetailsService {

    private final AppUserApi appUserApi;

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUserAuthenticationData userAuthData = appUserApi.getUserAuthData(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return AppUserDetails.builder()
                .appUserId(userAuthData.appUserId())
                .username(userAuthData.email())
                .password(userAuthData.password())
                .enabled(userAuthData.isEnabled())
                .authority(new SimpleGrantedAuthority(userAuthData.role().name()))
                .build();
    }
}
