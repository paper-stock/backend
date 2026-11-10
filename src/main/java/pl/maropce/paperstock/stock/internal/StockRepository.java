package pl.maropce.paperstock.stock.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {

    @Query("""
                SELECT s
                FROM Stock s
                WHERE
                (
                    LOWER(s.symbol) LIKE LOWER(CONCAT('%', :symbol, '%'))
                    OR
                    LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))
                    OR
                    LOWER(s.exchange) LIKE LOWER(CONCAT('%', :exchange, '%'))
                )
                AND s.status = 'ACTIVE'
            """)
    List<Stock> searchActiveStocks(
            String symbol,
            String name,
            String exchange
    );

    Optional<Stock> findStockBySymbol(String symbol);

    List<Stock> findAllBySymbolIn(List<String> symbols);

    @Query("""
                SELECT s.symbol
                FROM Stock s
                WHERE s.symbol IN :symbols
            """)
    List<String> findAllSymbols(List<String> symbols);

    @Query("""
                SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
                FROM Stock s
                WHERE s.symbol = :symbol
                  AND s.status = 'ACTIVE'
            """)
    boolean existsActiveStockBySymbol(@Param("symbol") String symbol);
}
