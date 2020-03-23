package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.factions.XFaction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionListAllies implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(args.getOne("factionName").isPresent()){

            listFactionAllies(src, args.getOne("factionName").get().toString().toLowerCase());

        }
        else{

            listFactionAllies(src, "");

        }

        return CommandResult.success();

    }

    private void listFactionAllies(CommandSource src, String targetFactionName){

        if(targetFactionName.equals("")){

            if(src instanceof Player){

                Player caller = (Player)src;

                Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

                if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

                optCallerFaction.get().listAllies(caller);

            }
            else{

                src.sendMessage(Text.of("\u00a7c[Factions] | As you are not a player, you need to specify a faction name"));

            }

        }
        else{

            Optional<XFaction> optTargetFaction = XFactionCommandManager.getFactionByName(targetFactionName);

            if(!optTargetFaction.isPresent()) { src.sendMessage(Text.of(XError.XERROR_XFNULL.getDesc())); return; }

            optTargetFaction.get().listAllies(src);

        }

    }

}
