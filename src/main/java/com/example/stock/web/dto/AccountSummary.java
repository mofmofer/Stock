package com.example.stock.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * アカウント情報をクライアントへ返却するためのDTOです。
 */
public record AccountSummary(
        UUID id,
        String ownerName,
        BigDecimal cashBalance,
        List<HoldingView> holdings,
        Instant createdAt
) {
}
