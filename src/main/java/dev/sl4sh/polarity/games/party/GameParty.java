package dev.sl4sh.polarity.games.party;

import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameParty {

    @Nonnull
    private final List<UUID> partyPlayers = new ArrayList<>();

    @Nonnull
    private UUID partyOwner;

    @Nonnull
    public List<UUID> getPartyPlayers() {
        return partyPlayers;
    }

    @Nonnull
    public UUID getPartyOwner() {
        return partyOwner;
    }

    public GameParty(@Nonnull UUID partyOwner) {
        this.partyOwner = partyOwner;
        partyPlayers.add(partyOwner);
    }

    public void add(@Nonnull Player player){

        if(!partyPlayers.contains(player.getUniqueId())){

            for(UUID partyPlayerID : partyPlayers){

                Optional<Player> partyPlayer = Utilities.getPlayerByUniqueID(partyPlayerID);

                if(partyPlayer.isPresent()){

                    partyPlayer.get().sendMessage(Text.of(TextColors.LIGHT_PURPLE, player.getName(), TextColors.AQUA, " just joined your party."));
                    partyPlayer.get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, partyPlayer.get().getPosition(), 0.25);

                }

            }

            partyPlayers.add(player.getUniqueId());

            if(Utilities.getPlayerByUniqueID(getPartyOwner()).isPresent()){

                player.sendMessage(Text.of(TextColors.AQUA, "You just joined ", TextColors.LIGHT_PURPLE, Utilities.getPlayerByUniqueID(getPartyOwner()).get().getName(), TextColors.AQUA, "'s party."));
                player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.getPosition(), 0.25);

            }

        }

    }

    public void remove(@Nonnull Player target){

        if(partyPlayers.contains(target.getUniqueId())){

            partyPlayers.remove(target.getUniqueId());

            if(getPartyPlayers().size() <= 1){

                destroy();
                return;

            }

            if(target.getUniqueId().equals(partyOwner) && partyPlayers.size() >= 1){

                Optional<Player> newOwner = Utilities.getPlayerByUniqueID(partyPlayers.get(0));

                if(newOwner.isPresent()){

                    newOwner.get().sendMessage(Text.of(TextColors.AQUA, "You are the new party owner."));
                    partyOwner = newOwner.get().getUniqueId();

                }

            }

        }

    }

    public void destroy(){

        this.partyOwner = UUID.randomUUID();
        this.partyPlayers.clear();

    }

}
