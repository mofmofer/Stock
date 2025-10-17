package com.example.stock.web.dto;

/**
 * 認証済みセッション情報を表現するレスポンスです。
 *
 * @param displayName 表示名
 */
public record SessionResponse(String displayName) {
}

