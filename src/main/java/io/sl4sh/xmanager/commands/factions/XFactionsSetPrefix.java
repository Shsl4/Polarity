package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XColor;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
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

public class XFactionsSetPrefix implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's prefix name."))
                .arguments(GenericArguments.string(Text.of("prefix")), GenericArguments.enumValue(Text.of("color"), XColor.class))
                .permission("xmanager.factions.setprefix")
                .executor(new XFactionsSetPrefix())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("prefix").isPresent() && args.getOne("color").isPresent()){

                String prefix = args.getOne("prefix").get().toString();
                setFactionPrefix(ply, prefix, (XColor)args.getOne("color").get());

            }
            else{

                setFactionPrefix(ply, "", XColor.WHITE);

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void setFactionPrefix(Player caller, String factionPrefix, XColor color){

        Optional<XFaction> optXFac = XUtilities.getPlayerFaction(caller);

        if(optXFac.isPresent()){

            XFaction xFac = optXFac.get();

            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(caller);

            if(!optPermData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            if(optPermData.get().getManage()){

                if(factionPrefix.equals("")){

                    xFac.setPrefix("");
                    caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully removed your faction's prefix."));

                }
                else{

                    if(XUtilities.getStringWithoutModifiers(factionPrefix).length() > 15){

                        caller.sendMessage(Text.of(XError.XERROR_LGPREFIX.getDesc()));
                        return;

                    }

                    xFac.setPrefix(color.getStringColor() + ("[" + XUtilities.getStringWithoutModifiers(factionPrefix) + "]"));
                    caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully updated your faction's prefix."));

                }

                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();
                return;

            }
            else{

                caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

            }

        }

        caller.sendMessage(XError.XERROR_NOXF.getDesc());


    }

}
