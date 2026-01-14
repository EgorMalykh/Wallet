package com.example.bankaccount.service;

import com.example.bankaccount.entity.Wallet;
import com.example.bankaccount.exception.InsufficientFundsException;
import com.example.bankaccount.exception.WalletNotFoundException;
import com.example.bankaccount.model.WalletBalanceResponseModel;
import com.example.bankaccount.model.WalletOperationRequestModel;
import com.example.bankaccount.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletBalanceResponseModel getWalletBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return new WalletBalanceResponseModel(wallet.getWalletId(), wallet.getBalance());
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3)
    @Transactional
    public WalletBalanceResponseModel deposit(WalletOperationRequestModel request) {
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        wallet = walletRepository.save(wallet);

        return new WalletBalanceResponseModel(wallet.getWalletId(), wallet.getBalance());
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3)
    @Transactional
    public WalletBalanceResponseModel withdraw(WalletOperationRequestModel request) {
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        wallet = walletRepository.save(wallet);

        return new WalletBalanceResponseModel(wallet.getWalletId(), wallet.getBalance());
    }
}