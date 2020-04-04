package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import javax.annotation.Nonnull;
import java.util.*;

public class XEconomyService implements EconomyService {

    private final Currency defaultCurrency = new XDollar();

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

        for(XAccount account : XManager.getAccounts()){

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

        if(XManager.getAccounts() == null) { return Optional.empty(); }

        for(XAccount account : XManager.getAccounts()){

            if(account.getUniqueId().equals(uuid)){

                return Optional.of(account);

            }

        }

        Optional<Player> optTargetPlayer = Sponge.getServer().getPlayer(uuid);

        XAccount newAccount;

        if(!optTargetPlayer.isPresent()) {

            Optional<XFaction> optFaction = XUtilities.getFactionByUniqueID(uuid);

            if(!optFaction.isPresent()) { return Optional.empty(); }

            newAccount = new XAccount(optFaction.get());

        }
        else{

            newAccount = new XAccount(optTargetPlayer.get());

        }

        XManager.getAccounts().add(newAccount);
        XManager.getXManager().writeAccountsConfigurationFile();

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
