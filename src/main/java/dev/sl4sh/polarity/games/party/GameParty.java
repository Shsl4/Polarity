package dev.sl4sh.polarity.games.party;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GameParty {

    @Nonnull
    private final List<Player> partyPlayers = new ArrayList<>();

    @Nonnull
    private final Player partyCreator;

    @Nonnull
    public List<Player> getPartyPlayers() {
        return partyPlayers;
    }

    @Nonnull
    public Player getPartyCreator() {
        return partyCreator;
    }

    public GameParty(@Nonnull Player partyCreator) {
        this.partyCreator = partyCreator;
        partyPlayers.add(partyCreator);
    }

    public void add(@Nonnull Player player){

        if(!partyPlayers.contains(player)){

            partyPlayers.add(player);
            player.sendMessage(Text.of(TextColors.AQUA, "You just joined ", TextColors.LIGHT_PURPLE, player.getName(), TextColors.AQUA, "'s party."));

            for(Player partyPlayer : partyPlayers){

                partyPlayer.sendMessage(Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.AQUA, " just joined your party."));

            }

        }

    }

    public void remove(@Nonnull Player target, Object source){

        if(partyPlayers.contains(target)){

            partyPlayers.remove(target);

            if(source == partyCreator){

                target.sendMessage(Text.of(TextColors.RED, "You've been kicked out of ", partyCreator.getName(), "'s party."));

            }
            else if(source == target){

                target.sendMessage(Text.of(TextColors.AQUA, "Successfully left ", partyCreator.getName(), "'s party."));

            }

        }

    }

}
