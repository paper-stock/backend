package pl.maropce.paperstock.wallet.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class WalletNotFoundException extends BaseException {
    public WalletNotFoundException(UUID appUserId) {
        super(HttpStatus.NOT_FOUND, "Wallet not found for user: " + appUserId);
    }
}
