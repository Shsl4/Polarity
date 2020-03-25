package io.sl4sh.xmanager.economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

public class XTransferResult implements TransferResult {

    private Account account;
    private Account accountTo;
    private Currency currency;
    private BigDecimal amount;
    private Set<Context> contexts;
    private ResultType result;
    private TransactionType type;

    public XTransferResult(Account account, Account accountTo, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType result, TransactionType type) {
        this.account = account;
        this.accountTo = accountTo;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.result = result;
        this.type = type;
    }


    @Override
    public Account getAccountTo() {
        return accountTo;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public Set<Context> getContexts() {
        return contexts;
    }

    @Override
    public ResultType getResult() {
        return result;
    }

    @Override
    public TransactionType getType() {
        return type;
    }
}
