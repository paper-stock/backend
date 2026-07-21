package pl.maropce.paperstock.appUser.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
interface AppUserRepository extends JpaRepository<AppUser, UUID> {
}
