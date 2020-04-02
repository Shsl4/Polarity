package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XManagerLocationData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XManagerSetInitialSpawnLocation implements CommandExecutor{

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets the player spawn location the first time they join the server"))
                .permission("xmanager.setinitialspawn")
                .executor(new XManagerSetInitialSpawnLocation())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XManager.getConfigData().setInitialSpawnLocation(new XManagerLocationData(caller.getWorld().toString(), caller.getPosition().toString()));

            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | New initial player spawn location set"));

            XManager.getXManager().writeMainDataConfigurationFile();

        }

        return CommandResult.success();

    }
}
