package pl.maropce.paperstock.appUserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.maropce.paperstock.appUserAccount.Role;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDetails implements UserDetails {

    private UUID id;
    private UUID appUserId;
    private String username;

    @JsonIgnore
    private String password;

    private boolean isEnabled;
    private Role role;

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(
                new SimpleGrantedAuthority(role.name())
        );
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

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
