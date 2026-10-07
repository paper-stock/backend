package pl.maropce.paperstock.stock.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("""
            SELECT s
            FROM Stock s
            WHERE LOWER(s.symbol) = LOWER(:symbol)
            AND s.status = 'ACTIVE'
            """)
    Optional<Stock> findActiveStockBySymbol(String symbol);

    List<Stock> findAllBySymbolIn(List<String> symbols);
}
