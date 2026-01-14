package com.example.bankaccount.controller;

import com.example.bankaccount.exception.InsufficientFundsException;
import com.example.bankaccount.exception.WalletNotFoundException;
import com.example.bankaccount.handler.GlobalExceptionHandler;
import com.example.bankaccount.model.WalletBalanceResponseModel;
import com.example.bankaccount.model.WalletOperationRequestModel;
import com.example.bankaccount.model.enums.OperationType;
import com.example.bankaccount.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(GlobalExceptionHandler.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UUID testWalletId = UUID.randomUUID();

    @Test
    void getWalletBalance_ValidId_ReturnsOk() throws Exception {
        WalletBalanceResponseModel response = new WalletBalanceResponseModel(testWalletId, BigDecimal.valueOf(100.00));
        when(walletService.getWalletBalance(testWalletId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/wallet/{walletId}", testWalletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    void getWalletBalance_WalletNotFound_ReturnsNotFound() throws Exception {
        when(walletService.getWalletBalance(testWalletId)).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallet/{walletId}", testWalletId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void depositAndWithdraw_DepositOperation_ReturnsOk() throws Exception {
        WalletBalanceResponseModel response = new WalletBalanceResponseModel(testWalletId, BigDecimal.valueOf(150.00));
        when(walletService.deposit(any())).thenReturn(response);

        WalletOperationRequestModel request = new WalletOperationRequestModel(
                testWalletId, OperationType.DEPOSIT, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(150.00));
    }

    @Test
    void depositAndWithdraw_WithdrawOperation_ReturnsOk() throws Exception {
        WalletBalanceResponseModel response = new WalletBalanceResponseModel(testWalletId, BigDecimal.valueOf(50.00));
        when(walletService.withdraw(any())).thenReturn(response);

        WalletOperationRequestModel request = new WalletOperationRequestModel(
                testWalletId, OperationType.WITHDRAW, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(testWalletId.toString()))
                .andExpect(jsonPath("$.balance").value(50.00));
    }

    @Test
    void depositAndWithdraw_InvalidOperationType_ReturnsBadRequest() throws Exception {
        WalletOperationRequestModel request = new WalletOperationRequestModel(
                testWalletId, null, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void depositAndWithdraw_InvalidOperationTypeValue_ReturnsBadRequest() throws Exception {
        String jsonPayload = """
            {
                "walletId": "%s",
                "operationType": "TRANSFER",
                "amount": 50.00
            }
            """.formatted(testWalletId);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void depositAndWithdraw_AmountNotPositive_ReturnsValidationError() throws Exception {
        WalletOperationRequestModel request = new WalletOperationRequestModel(
                testWalletId, OperationType.DEPOSIT, BigDecimal.valueOf(-10.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void depositAndWithdraw_InsufficientFunds_ReturnsConflict() throws Exception {
        when(walletService.withdraw(any())).thenThrow(InsufficientFundsException.class);

        WalletOperationRequestModel request = new WalletOperationRequestModel(
                testWalletId, OperationType.WITHDRAW, BigDecimal.valueOf(200.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void depositAndWithdraw_MissingWalletId_ReturnsBadRequest() throws Exception {
        WalletOperationRequestModel request = new WalletOperationRequestModel(
                null, OperationType.DEPOSIT, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
