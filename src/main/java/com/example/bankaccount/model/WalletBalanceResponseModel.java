package com.example.bankaccount.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.UUID;

public record WalletBalanceResponseModel(
        UUID walletId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal balance
) {}