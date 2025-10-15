package com.example.stock.service;

import com.example.stock.exception.AccountNotFoundException;
import com.example.stock.exception.InsufficientFundsException;
import com.example.stock.exception.InvalidTradeException;
import com.example.stock.model.Account;
import com.example.stock.model.Holding;
import com.example.stock.model.TradeSide;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * アカウントおよび取引を管理するサービス層です。
 */
@Service
public class AccountService {
    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);
    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    /**
     * 新しいアカウントを作成します。
     *
     * @param ownerName 口座名義
     * @param initialDeposit 初期入金額（省略可）
     * @return 作成されたアカウント
     */
    public Account createAccount(String ownerName, BigDecimal initialDeposit) {
        BigDecimal startingBalance = initialDeposit == null ? BigDecimal.ZERO : initialDeposit;
        Account account = new Account(UUID.randomUUID(), ownerName, startingBalance);
        accounts.put(account.getId(), account);
        return account;
    }

    /**
     * アカウントIDを指定してアカウントを取得します。
     *
     * @param id アカウント識別子
     * @return 該当アカウント
     * @throws AccountNotFoundException アカウントが存在しない場合
     */
    public Account getAccount(UUID id) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    /**
     * 指定したアカウントに入金します。
     *
     * @param id アカウント識別子
     * @param amount 入金額
     * @return 更新後のアカウント
     */
    public Account deposit(UUID id, BigDecimal amount) {
        Account account = getAccount(id);
        synchronized (account) {
            account.setCashBalance(account.getCashBalance().add(amount, MATH_CONTEXT));
            return account;
        }
    }

    /**
     * 指定したアカウントから出金します。
     *
     * @param id アカウント識別子
     * @param amount 出金額
     * @return 更新後のアカウント
     * @throws InsufficientFundsException 残高が不足している場合
     */
    public Account withdraw(UUID id, BigDecimal amount) {
        Account account = getAccount(id);
        synchronized (account) {
            if (account.getCashBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(id, amount, account.getCashBalance());
            }
            account.setCashBalance(account.getCashBalance().subtract(amount, MATH_CONTEXT));
            return account;
        }
    }

    /**
     * 指定したアカウントで売買注文を約定させます。
     *
     * @param id アカウント識別子
     * @param side 売買区分
     * @param symbol 銘柄コード
     * @param exchange 取引市場
     * @param quantity 取引数量
     * @param pricePerShare 取引単価
     * @return 更新後のアカウント
     * @throws InvalidTradeException 取引内容が不正な場合
     * @throws InsufficientFundsException 買付時に残高不足となった場合
     */
    public Account executeTrade(UUID id, TradeSide side, String symbol, String exchange,
                                BigDecimal quantity, BigDecimal pricePerShare) {
        if (quantity.signum() <= 0 || pricePerShare.signum() <= 0) {
            throw new InvalidTradeException("Quantity and price must be positive");
        }
        Account account = getAccount(id);
        synchronized (account) {
            BigDecimal grossAmount = pricePerShare.multiply(quantity, MATH_CONTEXT);
            Map<String, Holding> holdings = account.getHoldingsInternal();
            String key = symbol.toUpperCase();
            Holding existing = holdings.get(key);

            if (side == TradeSide.BUY) {
                if (account.getCashBalance().compareTo(grossAmount) < 0) {
                    throw new InsufficientFundsException(id, grossAmount, account.getCashBalance());
                }
                account.setCashBalance(account.getCashBalance().subtract(grossAmount, MATH_CONTEXT));
                if (existing == null) {
                    existing = new Holding(key, exchange, quantity, pricePerShare);
                    holdings.put(key, existing);
                } else {
                    BigDecimal currentQuantity = existing.getQuantity();
                    BigDecimal newQuantity = currentQuantity.add(quantity, MATH_CONTEXT);
                    BigDecimal totalCost = existing.getAverageCost().multiply(currentQuantity, MATH_CONTEXT)
                            .add(pricePerShare.multiply(quantity, MATH_CONTEXT), MATH_CONTEXT);
                    BigDecimal newAverageCost = totalCost.divide(newQuantity, MATH_CONTEXT);
                    existing.setQuantity(newQuantity);
                    existing.setAverageCost(newAverageCost);
                }
            } else {
                if (existing == null) {
                    throw new InvalidTradeException("Cannot sell holdings that do not exist");
                }
                if (existing.getQuantity().compareTo(quantity) < 0) {
                    throw new InvalidTradeException("Cannot sell more than the available quantity");
                }
                BigDecimal newQuantity = existing.getQuantity().subtract(quantity, MATH_CONTEXT);
                account.setCashBalance(account.getCashBalance().add(grossAmount, MATH_CONTEXT));
                if (newQuantity.signum() == 0) {
                    holdings.remove(key);
                } else {
                    existing.setQuantity(newQuantity);
                }
            }
            return account;
        }
    }
}
