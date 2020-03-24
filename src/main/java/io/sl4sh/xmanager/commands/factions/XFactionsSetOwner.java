package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
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

public class XFactionsSetOwner implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's owner."))
                .permission("xmanager.factions.setowner")
                .arguments(GenericArguments.player(Text.of("playerName")))
                .executor(new XFactionsSetOwner())
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

                ply.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc()));

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void setFactionOwner(Player caller, Player newOwner){

        if(caller == newOwner) { caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | You are already the owner of this faction.")); return; }

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = optCallerFaction.get();

        Optional<XFaction> optTargetFaction = XUtilities.getPlayerFaction(newOwner);

        if(!optTargetFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        if(optTargetFaction.get() != callerFaction) { caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc()); return; }

        if(callerFaction.isOwner(caller.getName())){

            if(callerFaction.setPermissionDataForPlayer(newOwner, new XFactionPermissionData(true, true, true))){

                callerFaction.setFactionOwner(newOwner.getName());
                XManager.getXManager().writeFactionsConfigurationFile();

                caller.sendMessage(Text.of(TextColors.GREEN , "[Factions] | Successfully set " , TextColors.LIGHT_PURPLE , newOwner.getName() , TextColors.GREEN , " as the new faction owner!"));
                newOwner.sendMessage(Text.of(TextColors.AQUA , "[Factions] | " , TextColors.LIGHT_PURPLE , caller.getName() , TextColors.AQUA , " just set you as the new owner of the faction!"));

            }
            else{

                caller.sendMessage(XError.XERROR_UNKNOWN.getDesc());

            }

        }
        else{

            caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

        }

    }

}
