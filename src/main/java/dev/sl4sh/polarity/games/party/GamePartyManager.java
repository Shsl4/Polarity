package dev.sl4sh.polarity.games.party;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GamePartyManager {

    @Nonnull
    private final List<GameParty> activeParties = new ArrayList<>();

    public Optional<GameParty> getPlayerParty(Player player){

        for(GameParty party : activeParties){

            if(party.getPartyPlayers().contains(player)){

                return Optional.of(party);

            }

        }

        return Optional.empty();

    }

    public void partyWithPlayer(Player caller, Player player){

        if(getPlayerParty(player).isPresent()) {

            caller.sendMessage(Text.of(TextColors.RED, "You are already a member of a party."));
            return;

        }

        if(getPlayerParty(player).isPresent()){

            getPlayerParty(player).get().add(caller);

        }
        else{

            GameParty newParty = new GameParty(player);
            newParty.add(caller);
            activeParties.add(newParty);

        }

    }

}
