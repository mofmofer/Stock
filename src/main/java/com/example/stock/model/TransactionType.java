package com.example.stock.model;

/**
 * 取引履歴の種別を表す列挙型です。
 */
public enum TransactionType {
    /** 現金の入金。 */
    DEPOSIT,

    /** 現金の出金。 */
    WITHDRAWAL,

    /** 株式の売買。 */
    TRADE
}
