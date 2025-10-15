package com.example.stock.exception;

import java.util.UUID;

/**
 * 指定したアカウントが見つからない場合に送出される例外です。
 */
public class AccountNotFoundException extends RuntimeException {
    /**
     * アカウントが見つからなかったことを表す例外を生成します。
     *
     * @param id 見つからなかったアカウントの識別子
     */
    public AccountNotFoundException(UUID id) {
        super("Account not found: " + id);
    }
}
