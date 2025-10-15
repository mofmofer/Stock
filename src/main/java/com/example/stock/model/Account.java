package com.example.stock.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 外国株式取引のためのアカウント情報を保持するモデルです。
 */
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "cash_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal cashBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("symbol ASC")
    private List<Holding> holdings = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected Account() {
        // JPA 用のデフォルトコンストラクタ
    }

    /**
     * アカウントを生成します。
     *
     * @param ownerName 口座名義
     * @param initialBalance 初期残高
     */
    public Account(String ownerName, BigDecimal initialBalance) {
        this.id = UUID.randomUUID();
        this.ownerName = Objects.requireNonNull(ownerName, "ownerName");
        this.cashBalance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
        this.createdAt = Instant.now();
    }

    @PrePersist
    void initialize() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (cashBalance == null) {
            cashBalance = BigDecimal.ZERO;
        }
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
     * 保有銘柄の読み取り専用リストを取得します。
     *
     * @return 保有銘柄情報
     */
    public List<Holding> getHoldings() {
        return Collections.unmodifiableList(holdings);
    }

    /**
     * 取引所持銘柄を追加します。
     *
     * @param holding 追加するホールディング
     */
    public void addHolding(Holding holding) {
        holding.setAccount(this);
        holdings.add(holding);
    }

    /**
     * 指定したホールディングを削除します。
     *
     * @param holding 削除対象のホールディング
     */
    public void removeHolding(Holding holding) {
        holdings.remove(holding);
        holding.setAccount(null);
    }

    /**
     * 指定銘柄の保有情報を取得します。
     *
     * @param symbol 銘柄コード
     * @return 該当する保有情報
     */
    public Optional<Holding> findHolding(String symbol) {
        return holdings.stream()
                .filter(holding -> holding.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
    }

    /**
     * 口座作成日時を取得します。
     *
     * @return 作成日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }
}
