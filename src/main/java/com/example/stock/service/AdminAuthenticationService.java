package com.example.stock.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 管理者向けの認証を提供するサービスです。
 */
@Service
public class AdminAuthenticationService {

    private final String validAdminId;
    private final String validPassword;
    private final String displayName;

    public AdminAuthenticationService(
            @Value("${app.admin-auth.id:admin}") String validAdminId,
            @Value("${app.admin-auth.password:admin-demo}") String validPassword,
            @Value("${app.admin-auth.display-name:管理者}") String displayName) {
        this.validAdminId = validAdminId == null ? "" : validAdminId.trim();
        this.validPassword = validPassword == null ? "" : validPassword;
        this.displayName = displayName == null ? "管理者" : displayName.trim();
    }

    /**
     * 管理者 ID とパスワードを検証し、認証済み管理者情報を返します。
     *
     * @param adminId  入力された管理者 ID
     * @param password 入力されたパスワード
     * @return 認証成功時は {@link AuthenticatedAdmin}、失敗時は {@link Optional#empty()}
     */
    public Optional<AuthenticatedAdmin> authenticate(String adminId, String password) {
        if (adminId == null || password == null) {
            return Optional.empty();
        }

        String normalizedAdminId = adminId.trim();
        if (normalizedAdminId.isEmpty()) {
            return Optional.empty();
        }

        if (normalizedAdminId.equals(validAdminId) && validPassword.equals(password)) {
            return Optional.of(new AuthenticatedAdmin(validAdminId, displayName));
        }

        return Optional.empty();
    }

    /**
     * セッションに保存する管理者情報です。
     */
    public record AuthenticatedAdmin(String adminId, String displayName) {
    }
}

