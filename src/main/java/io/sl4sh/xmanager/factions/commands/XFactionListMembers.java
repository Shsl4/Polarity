package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionListMembers implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;
            String factionArg = "";

            if(args.getOne("factionName").isPresent()){

                factionArg = args.getOne("factionName").get().toString().toLowerCase();

            }

            if(factionArg.equals("")){

                if(XFactionCommandManager.getPlayerFaction(ply) != null){

                    XFactionCommandManager.getPlayerFaction(ply).listMembers(ply);

                }
                else{

                    src.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));

                }

            }
            else{

                if(XFactionCommandManager.doesFactionExist(factionArg)){

                    XFactionCommandManager.getFaction(factionArg).listMembers(ply);

                }
                else{

                    src.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc()));

                }

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

}
