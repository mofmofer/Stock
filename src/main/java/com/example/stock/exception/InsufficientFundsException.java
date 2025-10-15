package com.example.stock.exception;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 残高不足により操作を完了できない場合に送出される例外です。
 */
public class InsufficientFundsException extends RuntimeException {
    /**
     * 残高不足の詳細情報を含む例外を生成します。
     *
     * @param accountId 該当アカウントの識別子
     * @param required 必要となる金額
     * @param available 利用可能な残高
     */
    public InsufficientFundsException(UUID accountId, BigDecimal required, BigDecimal available) {
        super("Insufficient funds for account " + accountId + ": required=" + required + ", available=" + available);
    }
}
