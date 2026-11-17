package pl.maropce.paperstock.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.wallet.internal.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletApi {

    private final WalletService walletService;

    public void deposit(UUID appUserId, BigDecimal amount) {
        walletService.deposit(appUserId, amount);
    }

    public void withdraw(UUID appUserId, BigDecimal amount) {
        walletService.withdraw(appUserId, amount);
    }

    public BigDecimal getWalletBalance(UUID appUserId) {
        return walletService.getWallet(appUserId).balance();
    }
}
