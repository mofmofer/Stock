package com.example.stock.web.auth;

/**
 * セッションに保存される属性名を管理します。
 */
public final class SessionAttributes {

    /** 認証済みユーザー情報を保持するセッションキー */
    public static final String AUTHENTICATED_USER = "AUTHENTICATED_USER";

    private SessionAttributes() {
    }
}

