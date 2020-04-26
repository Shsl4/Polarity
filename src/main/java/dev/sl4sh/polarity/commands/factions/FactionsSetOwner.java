package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class FactionsSetOwner implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's owner."))
                .permission("polarity.factions.setowner")
                .arguments(GenericArguments.player(Text.of("playerName")))
                .executor(new FactionsSetOwner())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("playerName").isPresent()){

                setFactionOwner(ply, (Player)args.getOne("playerName").get());

            }
            else{

                ply.sendMessage(Text.of(PolarityErrors.NULLPLAYER.getDesc()));

            }

        }
        else{

            src.sendMessage(Text.of(PolarityErrors.PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void setFactionOwner(Player caller, Player newOwner){

        if(caller == newOwner) { caller.sendMessage(Text.of(TextColors.AQUA, "You are already the owner of this faction.")); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

        Faction callerFaction = optCallerFaction.get();

        Optional<Faction> optTargetFaction = Utilities.getPlayerFaction(newOwner);

        if(!optTargetFaction.isPresent()) { caller.sendMessage(PolarityErrors.FACTION_NOTAMEMBER.getDesc()); return; }

        if(optTargetFaction.get() != callerFaction) { caller.sendMessage(PolarityErrors.FACTION_NOTAMEMBER.getDesc()); return; }

        if(callerFaction.isOwner(caller)){

            if(callerFaction.setPermissionDataForPlayer(newOwner, new FactionPermissionData(true, true, true))){

                callerFaction.setOwner(newOwner.getUniqueId());
                Polarity.getPolarity().writeAllConfig();

                caller.sendMessage(Text.of(TextColors.GREEN , "Successfully set " , TextColors.LIGHT_PURPLE , newOwner.getName() , TextColors.GREEN , " as the new faction owner!"));
                newOwner.sendMessage(Text.of(TextColors.AQUA , "" , TextColors.LIGHT_PURPLE , caller.getName() , TextColors.AQUA , " just set you as the new owner of the faction!"));

            }
            else{

                caller.sendMessage(PolarityErrors.UNKNOWN.getDesc());

            }

        }
        else{

            caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc());

        }

    }

}
