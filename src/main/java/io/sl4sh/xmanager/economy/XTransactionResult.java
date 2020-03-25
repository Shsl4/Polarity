package io.sl4sh.xmanager.economy;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import java.math.BigDecimal;
import java.util.Set;

public class XTransactionResult implements TransactionResult {

    private Account account;
    private Currency currency;
    private BigDecimal amount;
    private Set<Context> contexts;
    private ResultType resultType;
    private TransactionType type;

    public XTransactionResult(Account account, Currency currency, BigDecimal amount, Set<Context> contexts, ResultType resultType, TransactionType type) {

        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.resultType = resultType;
        this.type = type;

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
        return resultType;
    }

    @Override
    public TransactionType getType() {
        return type;
    }
}
