package dev.sl4sh.polarity.economy;

import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.economy.transactionidentifiers.AdminIdentifier;
import dev.sl4sh.polarity.economy.transactionidentifiers.PlayRewardIdentifier;
import dev.sl4sh.polarity.economy.transactionidentifiers.SellIdentifier;
import dev.sl4sh.polarity.economy.transactionidentifiers.GameIdentifier;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Polarity;
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
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ConfigSerializable
public class PolarityAccount implements UniqueAccount, Serializable {

    @Setting(value = "ownerUUID")
    private UUID ownerUUID = UUID.randomUUID();

    @Setting(value = "ownerName")
    private String ownerName = "None";

    @Setting(value = "accountBalance")
    private float accountBalance = 0;

    public PolarityAccount() {}

    public PolarityAccount(Player player){

        ownerUUID = player.getUniqueId();
        ownerName = player.getName();

    }

    public PolarityAccount(Faction faction){

        ownerUUID = faction.getUniqueId();
        ownerName = faction.getName();
        accountBalance = 1000;

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

        if(currency instanceof PolarityCurrency){

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
        balances.put(new PolarityCurrency(), BigDecimal.valueOf(accountBalance));
        return balances;

    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof PolarityCurrency){

            accountBalance = amount.floatValue();

            Polarity.getPolarity().writeAllConfig();

            return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);

        }

        return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);

    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause, Set<Context> contexts) {

        setBalance(new PolarityCurrency(), BigDecimal.ZERO, cause, contexts);
        PolarityCurrency cr = new PolarityCurrency();
        Map<Currency, TransactionResult> balances = new HashMap<Currency, TransactionResult>();
        balances.put(cr, new PolarityTransactionResult(this, cr, BigDecimal.ZERO, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW));
        return balances;

    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause, Set<Context> contexts) {
        return setBalance(new PolarityCurrency(), BigDecimal.ZERO, cause, contexts);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof PolarityCurrency){

            setBalance(currency, amount.add(BigDecimal.valueOf(accountBalance)), cause, contexts);

            printIncomeMessage(currency, amount, cause);

            return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);

        }

        return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);

    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause, Set<Context> contexts) {

        if(currency instanceof PolarityCurrency){

            if(this.hasEnoughBalanceToWithdraw(amount)){

                setBalance(currency, BigDecimal.valueOf(accountBalance - amount.floatValue()), cause, contexts);
                return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);

            }

            return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);

        }

        return new PolarityTransactionResult(this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);

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

        return new PolarityTransferResult(this, to, currency, amount, contexts, result.getResult(), TransactionTypes.TRANSFER);

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
        if(Sponge.getServer().getPlayer(ownerUUID).isPresent() && currency instanceof PolarityCurrency){

            PolarityCurrency dollarCurrency = (PolarityCurrency)currency;
            Player targetPlayer = Sponge.getServer().getPlayer(ownerUUID).get();

            targetPlayer.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, targetPlayer.getPosition(), .25);

            if(cause.allOf(AdminIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account by an administrator!"));
                return;

            }

            if(cause.allOf(PlayRewardIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account for being online!"));
                return;

            }

            if(cause.allOf(SellIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account for selling items!"));
                return;

            }

            if(cause.allOf(GameIdentifier.class).size() > 0){

                targetPlayer.sendMessage(Text.of(TextColors.AQUA, "", dollarCurrency.format(amount, 2), TextColors.AQUA, " have been added to your account for winning a game! Well done!"));
                return;

            }

            if(cause.containsType(Player.class)){

                if(cause.first(Player.class).isPresent()){

                    Player senderPlayer = cause.first(Player.class).get();

                    if(senderPlayer.equals(targetPlayer)) { return; }

                    targetPlayer.sendMessage(Text.of(TextColors.AQUA, "You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from ", senderPlayer.getName(), "!"));
                    return;

                }

            }

            if(cause.containsType(Faction.class)){

                if(cause.first(Faction.class).isPresent()){

                    Faction senderFaction = cause.first(Faction.class).get();

                    targetPlayer.sendMessage(Text.of(TextColors.AQUA, "You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, " from ", senderFaction.getDisplayName(), TextColors.AQUA, " faction!"));
                    return;

                }

            }

            targetPlayer.sendMessage(Text.of(TextColors.AQUA, "You just received ", dollarCurrency.format(amount, 2), TextColors.AQUA, "!"));

        }

    }

}
