package com.example.bankaccount.controller;

import com.example.bankaccount.model.WalletBalanceResponseModel;
import com.example.bankaccount.model.WalletOperationRequestModel;
import com.example.bankaccount.model.enums.OperationType;
import com.example.bankaccount.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletBalanceResponseModel> getWalletBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(walletService.getWalletBalance(walletId));
    }

    @PostMapping()
    public ResponseEntity<WalletBalanceResponseModel> depositAndWithdraw(
            @RequestBody @Valid WalletOperationRequestModel request) {
        if (request.operationType() == OperationType.WITHDRAW)
            return ResponseEntity.ok(walletService.withdraw(request));
        return ResponseEntity.ok(walletService.deposit(request));

    }
}