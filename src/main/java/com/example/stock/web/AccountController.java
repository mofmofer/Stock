package com.example.stock.web;

import com.example.stock.model.Account;
import com.example.stock.service.AccountService;
import com.example.stock.web.dto.AccountSummary;
import com.example.stock.web.dto.CashTransferRequest;
import com.example.stock.web.dto.CreateAccountRequest;
import com.example.stock.web.dto.HoldingView;
import com.example.stock.web.dto.TradeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * アカウント関連のREST APIを提供するコントローラーです。
 */
@RestController
@RequestMapping("/api/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    /**
     * コントローラーを初期化します。
     *
     * @param accountService アカウントサービス
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 登録済みのアカウントを一覧取得します。
     *
     * @return アカウント要約のリスト
     */
    @GetMapping
    public List<AccountSummary> listAccounts() {
        return accountService.getAccounts().stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * 新しいアカウントを作成します。
     *
     * @param request 作成リクエスト
     * @return 作成されたアカウントの要約
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountSummary createAccount(@Valid @RequestBody CreateAccountRequest request) {
        BigDecimal initialDeposit = request.initialDeposit() == null ? BigDecimal.ZERO : request.initialDeposit();
        Account account = accountService.createAccount(request.ownerName(), initialDeposit);
        return toSummary(account);
    }

    /**
     * アカウント詳細を取得します。
     *
     * @param id アカウント識別子
     * @return アカウントの要約
     */
    @GetMapping("/{id}")
    public AccountSummary getAccount(@PathVariable UUID id) {
        return toSummary(accountService.getAccount(id));
    }

    /**
     * 指定アカウントに入金します。
     *
     * @param id アカウント識別子
     * @param request 入金リクエスト
     * @return 更新後のアカウント要約
     */
    @PostMapping("/{id}/deposit")
    public AccountSummary deposit(@PathVariable UUID id, @Valid @RequestBody CashTransferRequest request) {
        return toSummary(accountService.deposit(id, request.amount()));
    }

    /**
     * 指定アカウントから出金します。
     *
     * @param id アカウント識別子
     * @param request 出金リクエスト
     * @return 更新後のアカウント要約
     */
    @PostMapping("/{id}/withdraw")
    public AccountSummary withdraw(@PathVariable UUID id, @Valid @RequestBody CashTransferRequest request) {
        return toSummary(accountService.withdraw(id, request.amount()));
    }

    /**
     * 売買注文を約定します。
     *
     * @param id アカウント識別子
     * @param request 取引リクエスト
     * @return 更新後のアカウント要約
     */
    @PostMapping("/{id}/trade")
    public AccountSummary trade(@PathVariable UUID id, @Valid @RequestBody TradeRequest request) {
        Account account = accountService.executeTrade(id, request.side(), request.symbol(), request.exchange(),
                request.quantity(), request.pricePerShare());
        return toSummary(account);
    }

    /**
     * アカウントモデルをAPIレスポンス用DTOへ変換します。
     *
     * @param account アカウントモデル
     * @return アカウントの要約
     */
    private AccountSummary toSummary(Account account) {
        List<HoldingView> holdings = account.getHoldings().stream()
                .map(holding -> new HoldingView(holding.getSymbol(), holding.getExchange(),
                        holding.getQuantity(), holding.getAverageCost()))
                .toList();
        return new AccountSummary(account.getId(), account.getOwnerName(), account.getCashBalance(), holdings,
                account.getCreatedAt());
    }
}
