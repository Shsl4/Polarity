package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionSetPrefix implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("prefix").isPresent()){

                String prefix = args.getOne("prefix").get().toString();
                setFactionPrefix(prefix, ply);

            }
            else{

                setFactionPrefix("", ply);

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void setFactionPrefix(String factionPrefix, Player ply){

        Optional<XFaction> optXFac = XFactionCommandManager.getPlayerFaction(ply);

        if(optXFac.isPresent()){

            XFaction xFac = optXFac.get();

            Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

            if(optPermData.get().getConfigure()){

                if(factionPrefix.equals("")){

                    xFac.setFactionPrefix("");
                    ply.sendMessage(Text.of("\u00a7a[Factions] | Successfully removed your faction's prefix."));

                }
                else{

                    if(XManager.getStringWithoutModifiers(factionPrefix).length() > 15){

                        ply.sendMessage(Text.of(XError.XERROR_LGPREFIX.getDesc()));
                        return;

                    }

                    xFac.setFactionPrefix(factionPrefix);
                    ply.sendMessage(Text.of("\u00a7a[Factions] | Successfully updated your faction's prefix."));

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
