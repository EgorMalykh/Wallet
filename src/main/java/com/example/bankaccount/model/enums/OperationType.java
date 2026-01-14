package com.example.bankaccount.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum OperationType {
    DEPOSIT,
    WITHDRAW;

    @JsonCreator
    public static OperationType decode(final String operation) {
        return Stream.of(OperationType.values())
                .filter(target -> target.name().equalsIgnoreCase(operation))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid operation type: " + operation)
                );
    }

    @JsonValue
    public String encode() {
        return this.name();
    }
}
