package pl.maropce.paperstock.position;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.position.internal.PositionService;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PositionApi {

    private final PositionService positionService;

    public PositionDTO getPosition(UUID appUserId, UUID stockId) {
        return positionService.getPosition(appUserId, stockId);
    }

    public void addPosition(AddNewPositionRequest addNewPositionRequest) {
        positionService.addPosition(addNewPositionRequest);
    }

    public void reserveShares(UUID appUserId, UUID id, BigDecimal shares) {
        positionService.reserveShares(appUserId, id, shares);
    }

    public void takeFromReservedShares(UUID appUserId, UUID stockId, BigDecimal sharesToSell) {
        positionService.takeFromReservedShares(appUserId, stockId, sharesToSell);
    }

    public void releaseReservedShares(UUID appUserId, UUID stockId, BigDecimal sharesToRelease) {
        positionService.releaseReservedShares(appUserId, stockId, sharesToRelease);
    }
}
