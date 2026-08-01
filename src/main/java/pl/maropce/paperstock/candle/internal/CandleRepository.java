package pl.maropce.paperstock.candle.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandleRepository extends JpaRepository<Candle, UUID> {

    List<Candle> findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestampAsc(
            String symbol,
            Timeframe timeframe,
            Instant from,
            Instant to
    );

    Optional<Candle> findFirstBySymbolAndTimeframeOrderByTimestampAsc(String symbol, Timeframe timeframe);

    Optional<Candle> findFirstBySymbolAndTimeframeOrderByTimestampDesc(String symbol, Timeframe timeframe);
}
