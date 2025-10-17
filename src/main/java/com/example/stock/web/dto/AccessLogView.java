package com.example.stock.web.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * アクセスログをフロントエンドへ返却する DTO です。
 */
public record AccessLogView(
        UUID id,
        String page,
        String path,
        String ipAddress,
        String userAgent,
        Instant accessedAt
) {
}

