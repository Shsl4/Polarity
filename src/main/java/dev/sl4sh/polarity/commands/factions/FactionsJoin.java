package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
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

public class FactionsJoin implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Joins a faction."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.join")
                .executor(new FactionsJoin())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("factionName").isPresent()){

                joinFaction(ply, args.getOne("factionName").get().toString());

            }
            else{

                // Will send the player an error message
                joinFaction(ply, "");

            }

        }
        else{

            src.sendMessage(Text.of(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void joinFaction(Player caller, String factionName){

        if(Utilities.doesFactionExistByName(factionName)){

            Optional<Faction> optFac = Utilities.getFactionByName(factionName);

            if(!optFac.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_XFNULL.getDesc()); return; }

            Faction targetFaction = optFac.get();

            if(targetFaction.getPlayerInvites().contains(caller.getUniqueId())){

                targetFaction.getMemberDataList().add(new FactionMemberData(caller.getUniqueId(), new FactionPermissionData(false, true, false)));

                targetFaction.getPlayerInvites().remove(caller.getUniqueId());

                caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully joined the faction ",  targetFaction.getDisplayName(), TextColors.GREEN, "!"));
                caller.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, caller.getPosition(), 0.75);

                for(FactionMemberData mbData : targetFaction.getMemberDataList()){

                    if(mbData.getPlayerUniqueID().equals(caller.getUniqueId())) { continue; }

                    Optional<Player> optPlayer = Utilities.getPlayerByUniqueID(mbData.getPlayerUniqueID());

                    if(optPlayer.isPresent()){

                        optPlayer.get().sendMessage(Text.of(TextColors.YELLOW, "[Factions] | ", TextColors.LIGHT_PURPLE, caller.getName(), TextColors.YELLOW, " just joined your faction!"));
                        optPlayer.get().playSound(SoundTypes.BLOCK_NOTE_BELL, optPlayer.get().getPosition(), 0.75);

                    }

                }

                Polarity.getPolarity().writeAllConfig();
                TabListManager.refreshTabLists();

            }
            else{

                caller.sendMessage(PolarityErrors.XERROR_NOTINVITED.getDesc());

            }

        }
        else{

            caller.sendMessage(PolarityErrors.XERROR_XFNULL.getDesc());

        }

    }

}
