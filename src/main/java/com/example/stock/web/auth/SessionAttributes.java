package com.example.stock.web.auth;

/**
 * セッションに保存される属性名を管理します。
 */
public final class SessionAttributes {

    /** 認証済みユーザー情報を保持するセッションキー */
    public static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";

    /** 認証済み管理者情報を保持するセッションキー */
    public static final String ADMIN_AUTHENTICATED_USER = "ADMIN_AUTHENTICATED_USER";

    private SessionAttributes() {
    }
}

