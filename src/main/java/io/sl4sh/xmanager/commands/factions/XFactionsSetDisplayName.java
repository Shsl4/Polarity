package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XColor;
import io.sl4sh.xmanager.enums.XError;
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

public class XFactionsSetDisplayName implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's display name."))
                .arguments(GenericArguments.string(Text.of("displayName")), GenericArguments.enumValue(Text.of("color"), XColor.class))
                .permission("xmanager.factions.setdisplayname")
                .executor(new XFactionsSetDisplayName())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("displayName").isPresent() && args.getOne("color").isPresent()){

                String displayName = args.getOne("displayName").get().toString();
                setFactionDisplayName(displayName, ply, (XColor)args.getOne("color").get());

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void setFactionDisplayName(String displayName, Player ply, XColor color){

        Optional<XFaction> optXFac = XUtilities.getPlayerFaction(ply);

        if(optXFac.isPresent()){

            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            if(optPermData.get().getManage()){

                if(displayName.equals("")){

                    optXFac.get().setDisplayName(optXFac.get().getName());
                    ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully removed your faction's display name."));

                }
                else{

                    optXFac.get().setDisplayName(color.getStringColor() + XUtilities.getStringWithoutModifiers(displayName));
                    ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully updated your faction's display name."));

                }

                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();
                return;

            }
            else{

                ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

            }

        }

        ply.sendMessage(XError.XERROR_NOXF.getDesc());

    }

}
