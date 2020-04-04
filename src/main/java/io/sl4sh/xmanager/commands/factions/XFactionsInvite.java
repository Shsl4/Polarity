package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
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

public class XFactionsInvite implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Invites a player to your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("xmanager.factions.invite")
                .executor(new XFactionsInvite())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;

            // If the argument exists (this should theoretically always be true)
            if(args.getOne("playerName").isPresent()){

                invitePlayer(ply, (Player)args.getOne("playerName").get());

            }
            else{

                // Will send the player an error message
                ply.sendMessage(XError.XERROR_NULLPLAYER.getDesc());

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void invitePlayer(Player caller, Player target){

        if(XUtilities.getPlayerFaction(caller).isPresent()){

            XFaction callerFac = XUtilities.getPlayerFaction(caller).get();

            Optional<XFactionPermissionData> optMemberData = XUtilities.getPlayerFactionPermissions(caller);

            if(!optMemberData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            if(optMemberData.get().getManage()){

                if(!XUtilities.getPlayerFaction(target).isPresent()){

                    callerFac.getPlayerInvites().add(target.getUniqueId());
                    caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully invited player '" , target.getName() , "' to your faction."));

                    target.sendMessage(Text.of(TextColors.GREEN,"[Factions] | You've been invited to join the faction '" , callerFac.getDisplayName(), TextColors.GREEN, "' by " , caller.getName() , ". Type /factions join " , callerFac.getName(), TextColors.GREEN, " to join the faction."));
                    XManager.getXManager().writeFactionsConfigurationFile();

                }
                else{

                    caller.sendMessage(XInfo.XERROR_XFEMEMBER.getDesc());

                }

            }
            else{

                caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            caller.sendMessage(XError.XERROR_NOXF.getDesc());

        }

    }

}
