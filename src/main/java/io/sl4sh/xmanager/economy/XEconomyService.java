package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.accounts.XFactionAccount;
import io.sl4sh.xmanager.economy.accounts.XPlayerAccount;
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

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public Set<Currency> getCurrencies() {

        Set<Currency> setVal = new HashSet<Currency>();
        setVal.add(getDefaultCurrency());
        return setVal;

    }

    @Override
    public boolean hasAccount(UUID uuid) {

        for(XPlayerAccount account : XManager.getPlayerAccounts()){

            if(account.getUniqueId().equals(uuid)){

                return true;

            }

        }

        return false;

    }

    @Override
    public boolean hasAccount(@Nonnull String identifier) {

        for(XPlayerAccount account : XManager.getPlayerAccounts()){

            if(account.getTargetPlayerName().equals(identifier)){

                return true;

            }

        }

        for(XFaction faction : XManager.getFactions()){

            if(faction.getFactionName().equals(identifier)){

                return faction.getFactionAccount().isPresent();

            }

        }

        return false;

    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid) {

        if(XManager.getPlayerAccounts() == null) { return Optional.empty(); }

        for(XPlayerAccount account : XManager.getPlayerAccounts()){

            if(account.getUniqueId().equals(uuid)){

                return Optional.of(account);

            }

        }

        Optional<Player> optTargetPlayer = Sponge.getServer().getPlayer(uuid);

        if(!optTargetPlayer.isPresent()) { return Optional.empty(); }

        Player targetPlayer = optTargetPlayer.get();

        XPlayerAccount newAccount = new XPlayerAccount(targetPlayer);

        XManager.getPlayerAccounts().add(newAccount);

        XManager.getXManager().writeAccountsConfigurationFile();

        return Optional.of(newAccount);

    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier) {

        for(XFaction faction : XManager.getFactions()){

            if(faction.getFactionName().equals(identifier)){

                return faction.getFactionAccount().isPresent() ? faction.getFactionAccount() : Optional.of(new XFactionAccount(identifier, 500));

            }

        }

        return Optional.empty();

    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator) {

    }

}
