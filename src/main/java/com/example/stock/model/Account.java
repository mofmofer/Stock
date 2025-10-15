package com.example.stock.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 外国株式取引のためのアカウント情報を保持するモデルです。
 */
public class Account {
    private final UUID id;
    private final String ownerName;
    private BigDecimal cashBalance;
    private final Map<String, Holding> holdings;
    private final Instant createdAt;

    /**
     * アカウントを生成します。
     *
     * @param id アカウント識別子
     * @param ownerName 口座名義
     * @param initialBalance 初期残高
     */
    public Account(UUID id, String ownerName, BigDecimal initialBalance) {
        this.id = Objects.requireNonNull(id, "id");
        this.ownerName = Objects.requireNonNull(ownerName, "ownerName");
        this.cashBalance = initialBalance;
        this.holdings = new LinkedHashMap<>();
        this.createdAt = Instant.now();
    }

    /**
     * アカウント識別子を取得します。
     *
     * @return アカウントID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 口座名義を取得します。
     *
     * @return 名義人名
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * 現金残高を取得します。
     *
     * @return 現金残高
     */
    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    /**
     * 現金残高を設定します。
     *
     * @param cashBalance 設定する残高
     */
    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    /**
     * 保有銘柄の読み取り専用マップを取得します。
     *
     * @return 保有銘柄情報
     */
    public Map<String, Holding> getHoldings() {
        return Collections.unmodifiableMap(holdings);
    }

    /**
     * ミューテーションを許可する保有銘柄マップを返します。
     *
     * @return 編集可能な保有銘柄マップ
     */
    public Map<String, Holding> getHoldingsInternal() {
        return holdings;
    }

    /**
     * 口座作成日時を取得します。
     *
     * @return 作成日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
