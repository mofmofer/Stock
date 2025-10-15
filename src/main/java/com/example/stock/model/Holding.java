package com.example.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * アカウントが保有する銘柄を表すモデルです。
 */
@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Column(name = "quantity", precision = 19, scale = 6, nullable = false)
    private BigDecimal quantity;

    @Column(name = "average_cost", precision = 19, scale = 6, nullable = false)
    private BigDecimal averageCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, columnDefinition = "TEXT")
    private Account account;

    protected Holding() {
        // JPA 用のデフォルトコンストラクタ
    }

    /**
     * 保有銘柄を生成します。
     *
     * @param symbol 銘柄コード
     * @param exchange 取引市場
     * @param quantity 保有数量
     * @param averageCost 平均取得単価
     */
    public Holding(String symbol, String exchange, BigDecimal quantity, BigDecimal averageCost) {
        this.symbol = Objects.requireNonNull(symbol, "symbol").toUpperCase();
        this.exchange = Objects.requireNonNull(exchange, "exchange");
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    public Long getId() {
        return id;
    }

    /**
     * 銘柄コードを取得します。
     *
     * @return 銘柄コード
     */
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = Objects.requireNonNull(symbol, "symbol").toUpperCase();
    }

    /**
     * 取引市場を取得します。
     *
     * @return 取引市場
     */
    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = Objects.requireNonNull(exchange, "exchange");
    }

    /**
     * 保有数量を取得します。
     *
     * @return 保有数量
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * 保有数量を設定します。
     *
     * @param quantity 設定する数量
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * 平均取得単価を取得します。
     *
     * @return 平均取得単価
     */
    public BigDecimal getAverageCost() {
        return averageCost;
    }

    /**
     * 平均取得単価を設定します。
     *
     * @param averageCost 設定する平均単価
     */
    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
