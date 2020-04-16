package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.tablist.TabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class FactionsDisband implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Disbands your faction."))
                .permission("polarity.factions.disband")
                .executor(new FactionsDisband())
                .build();
    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player caller = (Player) src;

            if(!disbandFaction(caller)){

                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.75);

            }

        }
        else{

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean disbandFaction(Player caller){

        Optional<Faction> optPendingDeleteFaction = Utilities.getPlayerFaction(caller);

        if(optPendingDeleteFaction.isPresent()){

            Faction pendingDeleteFaction = optPendingDeleteFaction.get();

            if(pendingDeleteFaction.getOwner().equals(caller.getUniqueId())){

                for(FactionMemberData memberData : pendingDeleteFaction.getMemberDataList()){

                    if(Utilities.getPlayerByUniqueID(memberData.getPlayerUniqueID()).isPresent()){

                        Player factionPlayer = Utilities.getPlayerByUniqueID(memberData.getPlayerUniqueID()).get();
                        factionPlayer.sendMessage(Text.of(TextColors.RED, "[Factions] | Your faction has been disbanded by the owner. (", caller.getName(), ")"));
                        factionPlayer.playSound(SoundTypes.AMBIENT_CAVE, factionPlayer.getPosition(), 0.75);

                    }

                }

                Polarity.getFactions().remove(pendingDeleteFaction);
                Polarity.getPolarity().writeAllConfig();
                TabListManager.refreshTabLists();
                return true;

            }
            else{

                caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | The faction can only be disbanded by the owner."));

            }

        }
        else{

            caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc());

        }

        return false;

    }

}
