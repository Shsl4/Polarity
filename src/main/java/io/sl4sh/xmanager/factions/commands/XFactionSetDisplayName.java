package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionSetDisplayName implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("displayName").isPresent()){

                String displayName = args.getOne("displayName").get().toString();
                setFactionDisplayName(displayName, ply);

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void setFactionDisplayName(String displayName, Player ply){

        XFaction xFac = XFactionCommandManager.getPlayerFaction(ply);

        if(xFac != null){

            if(XFactionCommandManager.getPlayerFactionPermissions(ply).getConfigure()){

                if(displayName.equals("")){

                    xFac.setFactionDisplayName(xFac.getFactionName());
                    ply.sendMessage(Text.of("\u00a7aSuccessfully removed your faction's display name."));

                }
                else{

                    xFac.setFactionDisplayName(displayName);
                    ply.sendMessage(Text.of("\u00a7aSuccessfully updated your faction's display name."));

                }

                XManager.getXManager().writeFactions();
                XTabListManager.refreshTabLists();
                return;

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

            }

        }

        ply.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));

    }

}
