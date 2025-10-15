package com.example.stock.web.dto;

import com.example.stock.model.TradeSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 取引実行リクエストを表すDTOです。
 */
public record TradeRequest(
        @NotNull(message = "Side is required")
        TradeSide side,
        @NotBlank(message = "Symbol is required")
        String symbol,
        @NotBlank(message = "Exchange is required")
        String exchange,
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0001", message = "Quantity must be positive")
        BigDecimal quantity,
        @NotNull(message = "Price per share is required")
        @DecimalMin(value = "0.0001", message = "Price per share must be positive")
        BigDecimal pricePerShare
) {
}
