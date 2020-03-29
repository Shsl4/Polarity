package io.sl4sh.xmanager.economy.accounts;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XTransactionResult;
import io.sl4sh.xmanager.economy.XTransferResult;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.transactiontypes.XTransactionTypes;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ConfigSerializable
public class XFactionAccount implements VirtualAccount {

    @Nonnull
    @Setting(value = "factionName")
    private String factionName = "None";

    @Setting(value = "factionBalance")
    private float factionBalance = 500;

    public XFactionAccount(String factionName, float factionBalance) {
        this.factionName = factionName;
        this.factionBalance = factionBalance;
    }

    public XFactionAccount () {}

    @Override
    public Text getDisplayName() {
        return Text.of(getFactionName() + "'s account");
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {

        return BigDecimal.valueOf(500.0f);

    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {

        if(currency instanceof XDollar){

            return getBalance(currency, contexts).floatValue() > 0;

        }

        return false;
    }

    @Override
    public BigDecimal getBalance(Currency currency, Set<Context> contexts) {
        return BigDecimal.valueOf(factionBalance);
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {
        Map<Currency, BigDecimal> balances = new HashMap<Currency, BigDecimal>();
        balances.put(new XDollar(), BigDecimal.valueOf(factionBalance));
        return balances;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            factionBalance = amount.floatValue();

            XManager.getXManager().writeAccountsConfigurationFile();

            return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Withdraw.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Withdraw.getTransactionType());

    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {

        setBalance(new XDollar(), BigDecimal.ZERO, cause, contexts);
        XDollar cr = new XDollar();
        Map<Currency, TransactionResult> balances = new HashMap<Currency, TransactionResult>();
        balances.put(cr, new XTransactionResult(this, cr, BigDecimal.ZERO, contexts, ResultType.SUCCESS, XTransactionTypes.Reset.getTransactionType()));
        return balances;

    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(new XDollar(), BigDecimal.ZERO, cause, contexts);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            setBalance(currency, amount.add(BigDecimal.valueOf(factionBalance)), cause, contexts);

            return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Deposit.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Deposit.getTransactionType());

    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            if(this.hasEnoughBalanceToWithdraw(amount)){

                setBalance(currency, BigDecimal.valueOf(factionBalance - amount.floatValue()), cause, contexts);
                return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Withdraw.getTransactionType());

            }

            return new XTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, XTransactionTypes.Withdraw.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Withdraw.getTransactionType());

    }

    private boolean hasEnoughBalanceToWithdraw(BigDecimal amount){

        return factionBalance - amount.floatValue() >= 0;

    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        TransactionResult result = this.withdraw(currency, amount, cause, contexts);

        if(result.getResult() == ResultType.SUCCESS){

            to.deposit(currency, amount, cause, contexts);

        }

        return new XTransferResult(this, to, currency, amount, contexts, result.getResult(), XTransactionTypes.Transfer.getTransactionType());

    }

    @Override
    public String getIdentifier() {
        return factionName;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    public String getFactionName() {
        return factionName;
    }

    public void setFactionName(String factionName) {
        this.factionName = factionName;
    }

    public float getFactionBalance() {
        return factionBalance;
    }

    public void setFactionBalance(float factionBalance) {
        this.factionBalance = factionBalance;
    }
}
