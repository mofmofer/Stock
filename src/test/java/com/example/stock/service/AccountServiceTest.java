package com.example.stock.service;

import com.example.stock.exception.InsufficientFundsException;
import com.example.stock.exception.InvalidTradeException;
import com.example.stock.model.Account;
import com.example.stock.model.TradeSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void createAccountShouldInitializeWithBalance() {
        Account account = accountService.createAccount("Alice", new BigDecimal("1000"));
        assertEquals(new BigDecimal("1000"), account.getCashBalance());
        assertEquals("Alice", account.getOwnerName());
        assertTrue(account.getHoldings().isEmpty());
    }

    @Test
    void buyTradeShouldReduceCashAndAddHolding() {
        Account account = accountService.createAccount("Bob", new BigDecimal("10000"));
        accountService.executeTrade(account.getId(), TradeSide.BUY, "SONY", "TYO", new BigDecimal("10"), new BigDecimal("95"));

        Account updated = accountService.getAccount(account.getId());
        assertEquals(new BigDecimal("10000").subtract(new BigDecimal("950")), updated.getCashBalance());
        assertTrue(updated.getHoldings().containsKey("SONY"));
        assertEquals(new BigDecimal("10"), updated.getHoldings().get("SONY").getQuantity());
    }

    @Test
    void sellTradeShouldIncreaseCashAndReduceHolding() {
        Account account = accountService.createAccount("Charlie", new BigDecimal("5000"));
        accountService.executeTrade(account.getId(), TradeSide.BUY, "TSM", "NYSE", new BigDecimal("5"), new BigDecimal("100"));
        accountService.executeTrade(account.getId(), TradeSide.SELL, "TSM", "NYSE", new BigDecimal("3"), new BigDecimal("120"));

        Account updated = accountService.getAccount(account.getId());
        assertEquals(new BigDecimal("5000").subtract(new BigDecimal("500")).add(new BigDecimal("360")), updated.getCashBalance());
        assertEquals(new BigDecimal("2"), updated.getHoldings().get("TSM").getQuantity());
    }

    @Test
    void sellingMoreThanHoldingShouldFail() {
        Account account = accountService.createAccount("Dana", new BigDecimal("2000"));
        accountService.executeTrade(account.getId(), TradeSide.BUY, "BABA", "NYSE", new BigDecimal("2"), new BigDecimal("80"));
        assertThrows(InvalidTradeException.class, () ->
                accountService.executeTrade(account.getId(), TradeSide.SELL, "BABA", "NYSE", new BigDecimal("3"), new BigDecimal("90")));
    }

    @Test
    void buyWithoutFundsShouldFail() {
        Account account = accountService.createAccount("Eve", new BigDecimal("100"));
        assertThrows(InsufficientFundsException.class, () ->
                accountService.executeTrade(account.getId(), TradeSide.BUY, "SHOP", "NYSE", new BigDecimal("10"), new BigDecimal("50")));
    }
}
