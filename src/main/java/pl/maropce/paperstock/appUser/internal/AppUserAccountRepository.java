package pl.maropce.paperstock.appUser.internal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface AppUserAccountRepository extends JpaRepository<AppUserAccount, UUID> {
    Optional<AppUserAccount> findByEmail(String email);
}
