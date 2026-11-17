package pl.maropce.paperstock.wallet.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.wallet.internal.dto.WalletDTO;
import pl.maropce.paperstock.wallet.internal.dto.WalletMapper;
import pl.maropce.paperstock.wallet.internal.exception.WalletNotFoundException;


import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Transactional
    public void createWallet(UUID appUserId) {
        if (walletRepository.existsByAppUserId(appUserId)) {
            log.warn("Wallet already exists for userId={}", appUserId);
            return;
        }

        log.debug("Creating wallet for userId={}", appUserId);

        Wallet wallet = Wallet.create(appUserId);

        walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public WalletDTO getWallet(UUID appUserId) {
        Wallet wallet = walletRepository.findByAppUserId(appUserId)
                .orElseThrow(() -> new WalletNotFoundException(appUserId));

        return walletMapper.toDTO(wallet);
    }

    @Transactional
    public void deposit(UUID appUserId, BigDecimal amount) {
        log.info("Wallet deposit: userId={}, amount={}", appUserId, amount);

        Wallet wallet = walletRepository.findWithLockByAppUserId(appUserId)
                .orElseThrow(() -> new WalletNotFoundException(appUserId));

        wallet.deposit(amount);

        walletRepository.save(wallet);
    }

    @Transactional
    public void withdraw(UUID appUserId, BigDecimal amount) {
        log.info("Wallet withdrawal: userId={}, amount={}", appUserId, amount);

        Wallet wallet = walletRepository.findWithLockByAppUserId(appUserId)
                .orElseThrow(() -> new WalletNotFoundException(appUserId));

        wallet.withdraw(amount);

        walletRepository.save(wallet);
    }
}
