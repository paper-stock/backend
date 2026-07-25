package pl.maropce.paperstock.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDetails implements UserDetails {

    private UUID appUserId;
    private String username;
    private String password;
    private boolean enabled;

    private GrantedAuthority authority;

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(authority);
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @NonNull
    @Override
    public String getUsername() {
        return username;
    }
}
