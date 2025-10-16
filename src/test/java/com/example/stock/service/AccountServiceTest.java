package com.example.stock.service;

import com.example.stock.exception.InsufficientFundsException;
import com.example.stock.exception.InvalidTradeException;
import com.example.stock.model.Account;
import com.example.stock.model.Holding;
import com.example.stock.model.TradeSide;
import com.example.stock.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

class AccountServiceTest {

    private AccountRepository accountRepository;
    private AccountService accountService;
    private Map<UUID, Account> store;

    @BeforeEach
    void setUp() {
        store = new HashMap<>();
        accountRepository = mock(AccountRepository.class);
        accountService = new AccountService(accountRepository);

        Answer<Account> saveAnswer = invocation -> {
            Account account = invocation.getArgument(0);
            store.put(account.getId(), account);
            return account;
        };
        lenient().when(accountRepository.save(any(Account.class))).thenAnswer(saveAnswer);
        lenient().when(accountRepository.findById(any(UUID.class)))
                .thenAnswer(invocation -> Optional.ofNullable(store.get(invocation.getArgument(0))));
        lenient().when(accountRepository.findAll())
                .thenAnswer(invocation -> new ArrayList<>(store.values()));
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
        Optional<Holding> sonyHolding = updated.findHolding("SONY");
        assertTrue(sonyHolding.isPresent());
        assertEquals(new BigDecimal("10"), sonyHolding.get().getQuantity());
    }

    @Test
    void sellTradeShouldIncreaseCashAndReduceHolding() {
        Account account = accountService.createAccount("Charlie", new BigDecimal("5000"));
        accountService.executeTrade(account.getId(), TradeSide.BUY, "TSM", "NYSE", new BigDecimal("5"), new BigDecimal("100"));
        accountService.executeTrade(account.getId(), TradeSide.SELL, "TSM", "NYSE", new BigDecimal("3"), new BigDecimal("120"));

        Account updated = accountService.getAccount(account.getId());
        assertEquals(new BigDecimal("5000").subtract(new BigDecimal("500")).add(new BigDecimal("360")), updated.getCashBalance());
        Optional<Holding> tsmHolding = updated.findHolding("TSM");
        assertTrue(tsmHolding.isPresent());
        assertEquals(new BigDecimal("2"), tsmHolding.get().getQuantity());
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
