package com.example.stock.service;

import com.example.stock.model.Account;
import com.example.stock.model.Holding;
import com.example.stock.model.TradeSide;
import com.example.stock.model.Transaction;
import com.example.stock.model.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * アカウントやポジション情報が SQLite データベースへ永続化されることを検証する統合テストです。
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountPersistenceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void accountLifecycleIsPersistedAcrossTransactions() {
        Account created = accountService.createAccount("Integration User", new BigDecimal("10000.00"));
        UUID id = created.getId();

        accountService.deposit(id, new BigDecimal("500.00"));
        accountService.executeTrade(id, TradeSide.BUY, "AAPL", "NASDAQ", new BigDecimal("5"), new BigDecimal("150.00"));
        accountService.executeTrade(id, TradeSide.SELL, "AAPL", "NASDAQ", new BigDecimal("2"), new BigDecimal("160.00"));

        entityManager.flush();
        entityManager.clear();

        Account reloaded = accountService.getAccount(id);
        assertEquals(new BigDecimal("10070.00"), reloaded.getCashBalance());

        Holding apple = reloaded.findHolding("AAPL").orElseThrow();
        assertEquals(new BigDecimal("3"), apple.getQuantity());
        assertEquals(new BigDecimal("150.00"), apple.getAverageCost());

        List<Transaction> transactions = accountService.getTransactions(id);
        assertEquals(3, transactions.size());
        Transaction latest = transactions.get(0);
        assertEquals(TransactionType.TRADE, latest.getType());
        assertEquals(TradeSide.SELL, latest.getTradeSide());
        assertEquals(0, latest.getGrossAmount().compareTo(new BigDecimal("320.00")));
        assertEquals(new BigDecimal("10070.00"), latest.getCashBalanceAfter());
    }

    @Test
    void listingAccountsReturnsPersistedHoldings() {
        Account account = accountService.createAccount("Snapshot User", new BigDecimal("2500.00"));
        accountService.executeTrade(account.getId(), TradeSide.BUY, "MSFT", "NASDAQ", new BigDecimal("4"), new BigDecimal("320.00"));

        entityManager.flush();
        entityManager.clear();

        List<Account> accounts = accountService.getAccounts();
        Account persisted = accounts.stream()
                .filter(a -> a.getId().equals(account.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(1, persisted.getHoldings().size());
        Holding holding = persisted.getHoldings().get(0);
        assertEquals("MSFT", holding.getSymbol());
        assertEquals(new BigDecimal("4"), holding.getQuantity());

        List<Transaction> history = accountService.getTransactions(account.getId());
        assertEquals(1, history.size());
        Transaction trade = history.get(0);
        assertEquals(TransactionType.TRADE, trade.getType());
        assertEquals(0, trade.getCashAmount().compareTo(new BigDecimal("-1280.00")));
    }
}
