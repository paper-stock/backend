package pl.maropce.paperstock.security.internal.auth.jwt.refreshToken;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    int deleteAllByExpiryDateBefore(Instant now);

    void deleteAllByUsername(@NonNull String username);
}
