package com.example.stock.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 保有している銘柄情報を表すモデルです。
 */
public class Holding {
    private final String symbol;
    private final String exchange;
    private BigDecimal quantity;
    private BigDecimal averageCost;

    /**
     * 保有銘柄情報を生成します。
     *
     * @param symbol 銘柄コード
     * @param exchange 上場市場
     * @param quantity 保有数量
     * @param averageCost 平均取得単価
     */
    public Holding(String symbol, String exchange, BigDecimal quantity, BigDecimal averageCost) {
        this.symbol = Objects.requireNonNull(symbol, "symbol");
        this.exchange = Objects.requireNonNull(exchange, "exchange");
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    /**
     * 銘柄コードを取得します。
     *
     * @return 銘柄コード
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * 上場市場を取得します。
     *
     * @return 上場市場
     */
    public String getExchange() {
        return exchange;
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
     * @param averageCost 設定する平均取得単価
     */
    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }
}
