package com.example.stock.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * アクセスログ記録のためのリクエスト DTO です。
 */
public record AccessLogRequest(
        @NotBlank(message = "page は必須です")
        @Size(max = 100, message = "page は100文字以内で指定してください")
        String page,

        @Size(max = 255, message = "path は255文字以内で指定してください")
        String path
) {
}

