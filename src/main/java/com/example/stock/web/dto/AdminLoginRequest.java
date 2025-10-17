package com.example.stock.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理者ログインリクエストを表現する DTO です。
 */
public record AdminLoginRequest(
        @NotBlank(message = "管理者 ID を入力してください。")
        String adminId,

        @NotBlank(message = "パスワードを入力してください。")
        String password
) {
}

