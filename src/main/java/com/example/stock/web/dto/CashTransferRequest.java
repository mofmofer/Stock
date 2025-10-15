package com.example.stock.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 入出金リクエストを表すDTOです。
 */
public record CashTransferRequest(
        @NotNull
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
}
