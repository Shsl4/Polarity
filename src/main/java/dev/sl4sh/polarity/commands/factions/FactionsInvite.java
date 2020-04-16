package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
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

public class FactionsInvite implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Invites a player to your faction."))
                .arguments(GenericArguments.player(Text.of("playerName")))
                .permission("polarity.factions.invite")
                .executor(new FactionsInvite())
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
                ply.sendMessage(PolarityErrors.XERROR_NULLPLAYER.getDesc());

            }

        }
        else{

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void invitePlayer(Player caller, Player target){

        if(Utilities.getPlayerFaction(caller).isPresent()){

            Faction callerFac = Utilities.getPlayerFaction(caller).get();

            Optional<FactionPermissionData> optMemberData = Utilities.getPlayerFactionPermissions(caller);

            if(!optMemberData.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

            if(optMemberData.get().getManage()){

                if(!Utilities.getPlayerFaction(target).isPresent()){

                    callerFac.getPlayerInvites().add(target.getUniqueId());
                    caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully invited player '" , target.getName() , "' to your faction."));

                    target.sendMessage(Text.of(TextColors.GREEN,"[Factions] | You've been invited to join the faction '" , callerFac.getDisplayName(), TextColors.GREEN, "' by " , caller.getName() , ". Type /factions join " , callerFac.getName(), TextColors.GREEN, " to join the faction."));
                    Polarity.getPolarity().writeAllConfig();

                }
                else{

                    caller.sendMessage(PolarityInfo.XERROR_XFEMEMBER.getDesc());

                }

            }
            else{

                caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc());

        }

    }

}
