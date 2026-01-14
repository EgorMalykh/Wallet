package com.example.bankaccount.service;

import com.example.bankaccount.entity.Wallet;
import com.example.bankaccount.exception.InsufficientFundsException;
import com.example.bankaccount.exception.WalletNotFoundException;
import com.example.bankaccount.model.WalletBalanceResponseModel;
import com.example.bankaccount.model.WalletOperationRequestModel;
import com.example.bankaccount.model.enums.OperationType;
import com.example.bankaccount.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletId = UUID.randomUUID();
        wallet = Wallet.builder().walletId(walletId).balance(BigDecimal.valueOf(100.00)).version(0L).build();
    }

    @Test
    void getWalletBalance_ValidId_ReturnsBalance() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WalletBalanceResponseModel result = walletService.getWalletBalance(walletId);

        assertNotNull(result);
        assertEquals(walletId, result.walletId());
        assertEquals(BigDecimal.valueOf(100.00), result.balance());
        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void getWalletBalance_NotFound_ThrowsException() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.getWalletBalance(walletId);
        });

        verify(walletRepository, times(1)).findById(walletId);
    }

    @Test
    void deposit_ValidAmount_UpdatesBalance() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletOperationRequestModel request = new WalletOperationRequestModel(walletId, OperationType.DEPOSIT, BigDecimal.valueOf(50.00));

        WalletBalanceResponseModel result = walletService.deposit(request);

        assertEquals(BigDecimal.valueOf(150.00), result.balance());
        verify(walletRepository, times(1)).save(argThat(w -> w.getBalance().equals(BigDecimal.valueOf(150.00))));
    }

    @Test
    void withdraw_ValidAmount_UpdatesBalance() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WalletOperationRequestModel request = new WalletOperationRequestModel(walletId, OperationType.WITHDRAW, BigDecimal.valueOf(30.00));

        WalletBalanceResponseModel result = walletService.withdraw(request);

        assertEquals(BigDecimal.valueOf(70.00), result.balance());
        verify(walletRepository, times(1)).save(argThat(w -> w.getBalance().equals(BigDecimal.valueOf(70.00))));
    }

    @Test
    void withdraw_InsufficientFunds_ThrowsException() {
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        WalletOperationRequestModel request = new WalletOperationRequestModel(walletId, OperationType.WITHDRAW, BigDecimal.valueOf(150.00));

        assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdraw(request);
        });

        verify(walletRepository, never()).save(any());
    }

    @Test
    void deposit_NullWalletId_ThrowsException() {
        WalletOperationRequestModel request = new WalletOperationRequestModel(null,OperationType.DEPOSIT, BigDecimal.valueOf(50.00));

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.deposit(request);
        });

        verify(walletRepository, never()).save(any());
    }

    @Test
    void withdraw_NullWalletId_ThrowsException() {
        WalletOperationRequestModel request = new WalletOperationRequestModel(null, OperationType.WITHDRAW, BigDecimal.valueOf(50.00));

        assertThrows(WalletNotFoundException.class, () -> {
            walletService.withdraw(request);
        });

        verify(walletRepository, never()).save(any());
    }
}
