package com.example.bankaccount.handler;

import com.example.bankaccount.exception.InsufficientFundsException;
import com.example.bankaccount.exception.InvalidOperationException;
import com.example.bankaccount.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleWalletNotFound(WalletNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseBody> handleInsufficientFunds(InsufficientFundsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request body");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseBody(errorMessage));
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseBody> handleInvalidOperation(InvalidOperationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseBody> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatus  httpStatus, Exception e) {
        return ResponseEntity.status(httpStatus).body(new ErrorResponseBody(e.getMessage()));
    }
}