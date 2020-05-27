package dev.sl4sh.polarity.games.party;

import org.spongepowered.api.effect.sound.SoundTypes;
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

            if(party.getPartyPlayers().contains(player.getUniqueId())){

                return Optional.of(party);

            }

        }

        return Optional.empty();

    }

    public void leaveParty(Player caller){

        if(getPlayerParty(caller).isPresent()){

            GameParty party = getPlayerParty(caller).get();
            party.remove(caller);
            caller.sendMessage(Text.of(TextColors.AQUA, "Successfully left your party."));
            caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.25);

            if(party.getPartyPlayers().size() <= 1){

                activeParties.remove(party);

            }

        }
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "You do not have any party."));

        }

    }

    public void kickFromParty(Player caller, Player target){

        if(getPlayerParty(caller).isPresent()){

            GameParty party = getPlayerParty(caller).get();

            if(getPlayerParty(target).isPresent() && party.equals(getPlayerParty(target).get())){

                if(party.getPartyOwner().equals(caller.getUniqueId())){

                    party.remove(target);
                    caller.sendMessage(Text.of(TextColors.AQUA, "Successfully kicked ", TextColors.LIGHT_PURPLE, target.getName(), TextColors.AQUA, " out of your party."));
                    caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.25);

                    if(party.getPartyPlayers().size() <= 1){

                        party.destroy();
                        activeParties.remove(party);

                    }

                }
                else{

                    caller.sendMessage(Text.of(TextColors.AQUA, "Only the party owner can kick members."));

                }

            }
            else{

                caller.sendMessage(Text.of(TextColors.AQUA, "This player is not in your party."));

            }

        }
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "You do not have any party."));

        }

    }

    public void joinPlayerParty(Player caller, Player joinTarget){

        if(getPlayerParty(caller).isPresent()) {

            caller.sendMessage(Text.of(TextColors.RED, "You are already a member of a party."));
            caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.25);
            return;

        }

        if(getPlayerParty(joinTarget).isPresent()){

            getPlayerParty(joinTarget).get().add(caller);

        }
        else{

            GameParty newParty = new GameParty(joinTarget.getUniqueId());
            newParty.add(caller);
            activeParties.add(newParty);

        }

    }

}
