package pl.maropce.paperstock.position.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    Optional<Position> findByAppUserIdAndStockId(UUID appUserId, UUID stockId);

    List<Position> findAllByAppUserId(UUID appUserId);
}
