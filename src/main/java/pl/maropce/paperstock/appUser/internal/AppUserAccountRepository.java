package pl.maropce.paperstock.appUser.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
interface AppUserAccountRepository extends JpaRepository<AppUserAccount, UUID> {
    Optional<AppUserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<AppUserAccount> findByAppUser_Id(UUID appUserId);
}
