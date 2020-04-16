package dev.sl4sh.polarity.economy;

import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import javax.annotation.Nonnull;
import java.util.*;

public class PolarityEconomyService implements EconomyService {

    private final Currency defaultCurrency = new PolarityCurrency();

    @Nonnull
    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Nonnull
    @Override
    public Set<Currency> getCurrencies() {

        Set<Currency> setVal = new HashSet<>();
        setVal.add(getDefaultCurrency());
        return setVal;

    }

    @Override
    public boolean hasAccount(@Nonnull UUID uuid) {

        for(PolarityAccount account : Polarity.getAccounts().getList()){

            if(account.getUniqueId().equals(uuid)){

                return true;

            }

        }

        return false;

    }

    @Override
    public boolean hasAccount(@Nonnull String identifier) {

        // Use the UUID function.
        return false;

    }

    @Nonnull
    @Override
    public Optional<UniqueAccount> getOrCreateAccount(@Nonnull UUID uuid) {

        for(PolarityAccount account : Polarity.getAccounts().getList()){

            if(account.getUniqueId().equals(uuid)){

                return Optional.of(account);

            }

        }

        Optional<Player> optTargetPlayer = Sponge.getServer().getPlayer(uuid);

        PolarityAccount newAccount;

        if(!optTargetPlayer.isPresent()) {

            Optional<Faction> optFaction = Utilities.getFactionByUniqueID(uuid);

            if(!optFaction.isPresent()) { return Optional.empty(); }

            newAccount = new PolarityAccount(optFaction.get());

        }
        else{

            newAccount = new PolarityAccount(optTargetPlayer.get());

        }

        Polarity.getAccounts().add(newAccount);
        Polarity.getPolarity().writeAllConfig();

        return Optional.of(newAccount);

    }

    @Nonnull
    @Override
    public Optional<Account> getOrCreateAccount(@Nonnull String identifier) {

        // Use UUIDs to create accounts as I want all of them to be identifiable.
        return Optional.empty();

    }

    @Override
    public void registerContextCalculator(@Nonnull ContextCalculator<Account> calculator) {

    }

}
