package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.data.XWorldInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XManagerProtectDimension implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Protects a dimension."))
                .permission("xmanager.protectdimension")
                .executor(new XManagerProtectDimension())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(caller.getWorld());

            if(!worldInfo.isWorldProtected()){

                worldInfo.setDimensionProtected(true);
                XManager.getXManager().writeWorldsInfoData();

                caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | New dimension protected!"));
                return CommandResult.success();

            }

            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | This dimension is already protected."));

        }

        return CommandResult.success();

    }

}
