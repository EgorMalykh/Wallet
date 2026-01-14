package com.example.bankaccount.repository;

import com.example.bankaccount.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    private final UUID walletId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    void shouldReturnWallet_WhenIdExists() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1000));

        walletRepository.save(wallet);

        assertTrue(walletRepository.findById(walletId).isPresent());
        Wallet retrieved = walletRepository.findById(walletId).get();
        assertEquals(BigDecimal.valueOf(1000), retrieved.getBalance());
    }

    @Test
    void shouldUpdateBalanceAfterDeposit() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1000));
        walletRepository.save(wallet);

        Wallet retrieved = walletRepository.findById(walletId).orElseThrow();
        retrieved.setBalance(retrieved.getBalance().add(BigDecimal.valueOf(500)));
        retrieved = walletRepository.save(retrieved);

        assertEquals(BigDecimal.valueOf(1500), retrieved.getBalance());
    }

    @Test
    void shouldUpdateBalanceAfterWithdraw() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1000));
        walletRepository.save(wallet);

        Wallet retrieved = walletRepository.findById(walletId).orElseThrow();
        retrieved.setBalance(retrieved.getBalance().subtract(BigDecimal.valueOf(500)));
        retrieved = walletRepository.save(retrieved);

        assertEquals(BigDecimal.valueOf(500), retrieved.getBalance());
    }
}
