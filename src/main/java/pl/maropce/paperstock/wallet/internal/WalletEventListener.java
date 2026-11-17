package pl.maropce.paperstock.wallet.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.auth.UserRegisteredEvent;
import pl.maropce.paperstock.payment.FundsDepositedEvent;

@Component
@RequiredArgsConstructor
class WalletEventListener {

    private final WalletService walletService;

    @ApplicationModuleListener
    void on(FundsDepositedEvent event) {
        walletService.deposit(event.appUserId(), event.amount());
    }

    @ApplicationModuleListener
    void on(UserRegisteredEvent event) {
        walletService.createWallet(event.appUserId());
    }

}
