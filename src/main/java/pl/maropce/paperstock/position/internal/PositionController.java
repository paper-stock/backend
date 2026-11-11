package pl.maropce.paperstock.position.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.position.PositionDTO;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<PositionDTO> getPositions(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        return positionService.getAllPositions(appUserDetails.getAppUserId());
    }
}
