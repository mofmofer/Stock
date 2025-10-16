package com.example.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * アカウントに紐付く入出金や売買のトランザクションを表すエンティティです。
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, columnDefinition = "TEXT")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_side", length = 8)
    private TradeSide tradeSide;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "exchange")
    private String exchange;

    @Column(name = "quantity", precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "price_per_share", precision = 19, scale = 6)
    private BigDecimal pricePerShare;

    @Column(name = "cash_amount", precision = 19, scale = 4, nullable = false)
    private BigDecimal cashAmount;

    @Column(name = "gross_amount", precision = 19, scale = 4)
    private BigDecimal grossAmount;

    @Column(name = "cash_balance_after", precision = 19, scale = 4, nullable = false)
    private BigDecimal cashBalanceAfter;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected Transaction() {
        // JPA 用のデフォルトコンストラクタ
    }

    private Transaction(Account account, TransactionType type, TradeSide tradeSide, String symbol, String exchange,
                         BigDecimal quantity, BigDecimal pricePerShare, BigDecimal cashAmount, BigDecimal grossAmount,
                         BigDecimal cashBalanceAfter, Instant occurredAt) {
        this.account = Objects.requireNonNull(account, "account");
        this.type = Objects.requireNonNull(type, "type");
        this.tradeSide = tradeSide;
        this.symbol = symbol != null ? symbol.toUpperCase() : null;
        this.exchange = exchange;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.cashAmount = Objects.requireNonNull(cashAmount, "cashAmount");
        this.grossAmount = grossAmount;
        this.cashBalanceAfter = Objects.requireNonNull(cashBalanceAfter, "cashBalanceAfter");
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    /**
     * 入出金取引のトランザクションを生成します。
     *
     * @param account トランザクション対象のアカウント
     * @param type トランザクション種別
     * @param amount 入出金額（正数）
     * @param balanceAfter 取引後の残高
     * @return 生成されたトランザクション
     */
    public static Transaction cash(Account account, TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(balanceAfter, "balanceAfter");
        BigDecimal normalized = TransactionType.WITHDRAWAL.equals(type) ? amount.negate() : amount;
        return new Transaction(account, type, null, null, null, null, null, normalized, null, balanceAfter, Instant.now());
    }

    /**
     * 株式の売買トランザクションを生成します。
     *
     * @param account トランザクション対象のアカウント
     * @param side 売買区分
     * @param symbol 銘柄コード
     * @param exchange 取引所
     * @param quantity 約定数量
     * @param pricePerShare 約定単価
     * @param grossAmount 約定金額（絶対値）
     * @param balanceAfter 取引後の残高
     * @return 生成されたトランザクション
     */
    public static Transaction trade(Account account, TradeSide side, String symbol, String exchange,
                                    BigDecimal quantity, BigDecimal pricePerShare, BigDecimal grossAmount,
                                    BigDecimal balanceAfter) {
        Objects.requireNonNull(side, "side");
        Objects.requireNonNull(symbol, "symbol");
        Objects.requireNonNull(exchange, "exchange");
        Objects.requireNonNull(quantity, "quantity");
        Objects.requireNonNull(pricePerShare, "pricePerShare");
        Objects.requireNonNull(grossAmount, "grossAmount");
        Objects.requireNonNull(balanceAfter, "balanceAfter");
        BigDecimal cashAmount = side == TradeSide.BUY ? grossAmount.negate() : grossAmount;
        return new Transaction(account, TransactionType.TRADE, side, symbol, exchange, quantity, pricePerShare,
                cashAmount, grossAmount, balanceAfter, Instant.now());
    }

    @PrePersist
    void onPersist() {
        if (occurredAt == null) {
            occurredAt = Instant.now();
        }
        if (symbol != null) {
            symbol = symbol.toUpperCase();
        }
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public TransactionType getType() {
        return type;
    }

    public TradeSide getTradeSide() {
        return tradeSide;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPricePerShare() {
        return pricePerShare;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public BigDecimal getCashBalanceAfter() {
        return cashBalanceAfter;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
