package pl.maropce.paperstock.appUserAccount.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.maropce.paperstock.appUserAccount.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserAccountRepository extends JpaRepository<AppUserAccount, UUID> {
    Optional<AppUserAccount> findByEmail(String username);

    boolean existsByEmail(String username);

    boolean existsByRole(Role role);
}