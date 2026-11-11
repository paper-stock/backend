package pl.maropce.paperstock.position.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.position.AddNewPositionRequest;
import pl.maropce.paperstock.position.PositionDTO;
import pl.maropce.paperstock.position.internal.dto.PositionMapper;
import pl.maropce.paperstock.position.internal.exception.PositionNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public List<PositionDTO> getAllPositions(UUID appUserId) {
        return positionRepository.findAllByAppUserId(appUserId).stream()
                .map(positionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PositionDTO getPosition(UUID appUserId, UUID stockId) {
        log.debug("Getting position for user {} and stock {}", appUserId, stockId);

        Position position = positionRepository.findByAppUserIdAndStockId(appUserId, stockId)
                .orElseThrow(() -> new PositionNotFoundException(appUserId, stockId));

        return positionMapper.toDTO(position);
    }

    @Transactional
    public void addPosition(AddNewPositionRequest addNewPositionRequest) {
        log.debug("Adding new position for user {} and stock {}", addNewPositionRequest.appUserId(), addNewPositionRequest.stockId());

        Optional<Position> optionalPosition = positionRepository.findByAppUserIdAndStockId(addNewPositionRequest.appUserId(), addNewPositionRequest.stockId());

        Position position;
        if (optionalPosition.isPresent()) {
            position = optionalPosition.get();
            position.setShares(position.getShares().add(addNewPositionRequest.shares()));
        } else {
            position = Position.builder()
                    .appUserId(addNewPositionRequest.appUserId())
                    .stockId(addNewPositionRequest.stockId())
                    .stockSymbol(addNewPositionRequest.stockSymbol())
                    .shares(addNewPositionRequest.shares())
                    .reservedShares(BigDecimal.ZERO)
                    .build();
        }

        positionRepository.save(position);
    }

    @Transactional
    public void reserveShares(UUID appUserId, UUID id, BigDecimal shares) {
        log.debug("Reserving {} shares for user {} and stock {}", shares, appUserId, id);

        Position position = positionRepository.findByAppUserIdAndStockId(appUserId, id)
                .orElseThrow(() -> new PositionNotFoundException(appUserId, id));

        if (position.getShares().compareTo(shares) < 0) {
            throw new IllegalArgumentException("Not enough shares to reserve");
        }

        position.setShares(position.getShares().subtract(shares));
        position.setReservedShares(position.getReservedShares().add(shares));

        positionRepository.save(position);
    }

    /**
     * Takes shares from the reserved shares of a user's position.
     * If the number of reserved shares becomes zero, the position is deleted.
     *
     * @param appUserId    the ID of the user
     * @param stockId      the ID of the stock
     * @param sharesToSell the number of shares to take from reserved
     * @throws PositionNotFoundException if the position does not exist
     * @throws IllegalArgumentException  if there are not enough reserved shares to take
     */
    @Transactional
    public void takeFromReservedShares(UUID appUserId, UUID stockId, BigDecimal sharesToSell) {
        log.debug("Taking {} shares from reserved for user {} and stock {}", sharesToSell, appUserId, stockId);

        Position position = positionRepository.findByAppUserIdAndStockId(appUserId, stockId)
                .orElseThrow(() -> new PositionNotFoundException(appUserId, stockId));

        if (position.getReservedShares().compareTo(sharesToSell) < 0) {
            throw new IllegalArgumentException("Not enough reserved shares to take");
        }


        BigDecimal actualReservedShares = position.getReservedShares().subtract(sharesToSell);

        if (actualReservedShares.compareTo(BigDecimal.ZERO) == 0
                && position.getShares().compareTo(BigDecimal.ZERO) == 0
        ) {
            log.debug("No more shares left for user {} and stock {}, deleting position", appUserId, stockId);
            positionRepository.delete(position);
        } else {
            position.setReservedShares(actualReservedShares);
            positionRepository.save(position);
        }
    }

    /**
     * Releases reserved shares back to the user's available shares.
     *
     * @param appUserId       the ID of the user
     * @param stockId         the ID of the stock
     * @param sharesToRelease the number of shares to release
     * @throws PositionNotFoundException if the position does not exist
     * @throws IllegalArgumentException  if there are not enough reserved shares to release
     */
    @Transactional
    public void releaseReservedShares(UUID appUserId, UUID stockId, BigDecimal sharesToRelease) {
        log.debug("Releasing {} reserved shares for user {} and stock {}", sharesToRelease, appUserId, stockId);
        Position position = positionRepository.findByAppUserIdAndStockId(appUserId, stockId)
                .orElseThrow(() -> new PositionNotFoundException(appUserId, stockId));

        if (position.getReservedShares().compareTo(sharesToRelease) < 0) {
            throw new IllegalArgumentException("Not enough reserved shares to release");
        }

        position.setReservedShares(position.getReservedShares().subtract(sharesToRelease));
        position.setShares(position.getShares().add(sharesToRelease));

        positionRepository.save(position);
    }
}
