package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XTransactionResult;
import io.sl4sh.xmanager.economy.XTransferResult;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.economy.transactionidentifiers.XAdminIdentifier;
import io.sl4sh.xmanager.economy.transactionidentifiers.XPlayRewardIdentifier;
import io.sl4sh.xmanager.economy.transactionidentifiers.XSellIdentifier;
import io.sl4sh.xmanager.economy.transactiontypes.XTransactionTypes;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
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
public class XAccount implements UniqueAccount{

    @Setting(value = "ownerUUID")
    private UUID ownerUUID = UUID.randomUUID();

    @Setting(value = "ownerName")
    private String ownerName = "None";

    @Setting(value = "accountBalance")
    private float accountBalance = 0;

    public XAccount() {}

    public XAccount(Player player){

        ownerUUID = player.getUniqueId();
        ownerName = player.getName();

    }

    public XAccount(XFaction faction){

        ownerUUID = faction.getUniqueId();
        ownerName = faction.getName();

    }

    public String getTargetPlayerName(){

        return ownerName;

    }

    @Override
    public Text getDisplayName() {
        return Text.of(ownerName, "'s Account");
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
        return BigDecimal.valueOf(accountBalance);
    }

    @Override
    public Map<Currency, BigDecimal> getBalances(Set<Context> contexts) {

        Map<Currency, BigDecimal> balances = new HashMap<Currency, BigDecimal>();
        balances.put(new XDollar(), BigDecimal.valueOf(accountBalance));
        return balances;

    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            accountBalance = amount.floatValue();

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

            setBalance(currency, amount.add(BigDecimal.valueOf(accountBalance)), cause, contexts);

            printIncomeMessage(currency, amount, cause);

            return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Deposit.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Deposit.getTransactionType());

    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof XDollar){

            if(this.hasEnoughBalanceToWithdraw(amount)){

                setBalance(currency, BigDecimal.valueOf(accountBalance - amount.floatValue()), cause, contexts);
                return new XTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, XTransactionTypes.Withdraw.getTransactionType());

            }

            return new XTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, XTransactionTypes.Withdraw.getTransactionType());

        }

        return new XTransactionResult(this, currency, amount, contexts, ResultType.FAILED, XTransactionTypes.Withdraw.getTransactionType());

    }

    private boolean hasEnoughBalanceToWithdraw(BigDecimal amount){

        return accountBalance - amount.floatValue() >= 0;

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
        return ownerName + "'s Account";
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null;
    }

    @Override
    public UUID getUniqueId() {
        return ownerUUID;
    }

    private void printIncomeMessage(Currency currency, BigDecimal amount, Cause cause){

        // Send income notifications to the owner if it is a player.
        if(Sponge.getServer().getPlayer(ownerUUID).isPresent() && currency instanceof XDollar){

            XDollar dollarCurrency = (XDollar)currency;
            Player targetPlayer = Sponge.getServer().getPlayer(ownerUUID).get();

            targetPlayer.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, targetPlayer.getPosition(), 0.75);

            if(cause.allOf(XAdminIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | ", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account by an administrator!"));
                return;

            }

            if(cause.allOf(XPlayRewardIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | ", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account for being online!"));
                return;

            }

            if(cause.allOf(XSellIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | ", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account for selling items!"));
                return;

            }

            if(cause.containsType(Player.class)){

                if(cause.first(Player.class).isPresent()){

                    Player senderPlayer = cause.first(Player.class).get();

                    if(senderPlayer.equals(targetPlayer)) { return; }

                    targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from ", senderPlayer.getName(), "!"));
                    return;

                }

            }

            if(cause.containsType(XFaction.class)){

                if(cause.first(XFaction.class).isPresent()){

                    XFaction senderFaction = cause.first(XFaction.class).get();

                    targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from ", senderFaction.getDisplayName(), TextColors.AQUA, " faction!"));
                    return;

                }

            }

            targetPlayer.sendMessage(Text.of(TextColors.AQUA, "[Economy] | You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, "!"));

        }

    }

}
