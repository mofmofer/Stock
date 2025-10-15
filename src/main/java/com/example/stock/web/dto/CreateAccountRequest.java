package com.example.stock.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * アカウント作成リクエストを表すDTOです。
 */
public record CreateAccountRequest(
        @NotBlank(message = "Owner name is required")
        String ownerName,
        @PositiveOrZero(message = "Initial deposit cannot be negative")
        BigDecimal initialDeposit
) {
}
