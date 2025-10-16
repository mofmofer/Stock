package com.example.stock.web.dto;

import com.example.stock.model.TradeSide;
import com.example.stock.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * API レスポンスで返すトランザクション情報を表す DTO です。
 */
public record TransactionView(
        Long id,
        TransactionType type,
        TradeSide tradeSide,
        String symbol,
        String exchange,
        BigDecimal quantity,
        BigDecimal pricePerShare,
        BigDecimal cashAmount,
        BigDecimal grossAmount,
        BigDecimal cashBalanceAfter,
        Instant occurredAt
) {
}
