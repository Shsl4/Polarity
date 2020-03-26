package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.transactiontypes.XTransactionTypes;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.*;

@ConfigSerializable
public class XPlayerAccount implements UniqueAccount{

    @Setting(value = "playerUUID")
    private UUID playerUUID = UUID.randomUUID();

    @Setting(value = "playerName")
    private String playerName = "None";

    @Setting(value = "playerBalance")
    private float playerBalance = 0;

    public XPlayerAccount() {}

    public XPlayerAccount(UUID playerUUID, String playerName, float playerBalance){

        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.playerBalance = playerBalance;

    }

    public XPlayerAccount(Player player){

        playerUUID = player.getUniqueId();
        playerName = player.getName();

    }

    public String getTargetPlayerName(){

        return playerName;

    }

    public Optional<Player> getTargetPlayer(){

        return Sponge.getServer().getPlayer(playerUUID);

    }

    @Override
    public Text getDisplayName() {
        return Text.of(playerName, "'s Account");
    }

    @Override
    public BigDecimal getDefaultBalance(Currency currency) {
        return BigDecimal.ZERO;
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
        return BigDecimal.valueOf(playerBalance);
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {

        Map<Currency, BigDecimal> balances = new HashMap<Currency, BigDecimal>();
        balances.put(new XDollar(), BigDecimal.valueOf(playerBalance));
        return balances;

    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            playerBalance = amount.floatValue();

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

            setBalance(currency, amount.add(BigDecimal.valueOf(playerBalance)), cause, contexts);

            printIncomeMessage(currency, amount, cause);

            return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Deposit.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Deposit.getTransactionType());

    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            if(this.hasEnoughBalanceToWithdraw(amount)){

                setBalance(currency, BigDecimal.valueOf(playerBalance - amount.floatValue()), cause, contexts);
                return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Withdraw.getTransactionType());

            }

            return new XTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, XTransactionTypes.Withdraw.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Withdraw.getTransactionType());

    }

    private boolean hasEnoughBalanceToWithdraw(BigDecimal amount){

        return playerBalance - amount.floatValue() >= 0;

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
        return playerName + "'s Account";
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return playerUUID;
    }

    private void printIncomeMessage(Currency currency, BigDecimal amount, Cause cause){

        if(currency instanceof XDollar && getTargetPlayer().isPresent()){

            XDollar dollarCurrency = (XDollar)currency;
            Player targetPlayer = getTargetPlayer().get();

            if(cause.allOf(XAdminIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | ", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account by an administrator!"));
                return;

            }

            if(cause.containsType(Player.class)){

                if(cause.first(Player.class).isPresent()){

                    Player senderPlayer = cause.first(Player.class).get();

                    targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from ", senderPlayer.getName(), "!"));
                    return;

                }

            }

            targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, "!"));

        }

    }

}
