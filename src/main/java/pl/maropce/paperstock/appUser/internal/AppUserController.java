package pl.maropce.paperstock.appUser.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.security.AppUserDetails;

@RestController
@RequestMapping("/api/app-users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/add-funds")
    public ResponseEntity<AppUserDTO> addFunds(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody @Valid AddFundsRequest request
    ) {
        return ResponseEntity.ok(
                appUserService.addFunds(appUserDetails.getAppUserId(), request)
        );
    }
}
