package com.example.stock.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * シンプルなログイン認証を提供するサービスです。
 */
@Service
public class AuthenticationService {

    private final String validEmail;
    private final String validPassword;
    private final String displayName;

    public AuthenticationService(
            @Value("${app.auth.email:user@example.com}") String validEmail,
            @Value("${app.auth.password:password123}") String validPassword,
            @Value("${app.auth.display-name:デモユーザー}") String displayName) {
        this.validEmail = validEmail == null ? "" : validEmail.trim();
        this.validPassword = validPassword == null ? "" : validPassword;
        this.displayName = displayName == null ? "ユーザー" : displayName.trim();
    }

    /**
     * メールアドレスとパスワードを検証し、認証済みユーザー情報を返します。
     *
     * @param email    入力されたメールアドレス
     * @param password 入力されたパスワード
     * @return 認証成功時は {@link AuthenticatedUser}、失敗時は {@link Optional#empty()}
     */
    public Optional<AuthenticatedUser> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (normalizedEmail.isEmpty()) {
            return Optional.empty();
        }

        if (normalizedEmail.equals(validEmail.toLowerCase()) && validPassword.equals(password)) {
            return Optional.of(new AuthenticatedUser(validEmail, displayName));
        }
        return Optional.empty();
    }

    /**
     * セッションへ保存する認証済みユーザー情報です。
     *
     * @param email       ログインに成功したユーザーのメールアドレス
     * @param displayName 表示名
     */
    public record AuthenticatedUser(String email, String displayName) {
    }
}

