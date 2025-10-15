package com.example.stock.web.dto;

import java.math.BigDecimal;

/**
 * 保有銘柄情報を返却するDTOです。
 */
public record HoldingView(
        String symbol,
        String exchange,
        BigDecimal quantity,
        BigDecimal averageCost
) {
}
