package com.example.bankaccount.model;

import com.example.bankaccount.model.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record WalletOperationRequestModel(

        @NotNull(message = "Wallet ID is required")
        UUID walletId,

        @NotNull(message = "Operation type is required")
        OperationType operationType,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal amount
) {}