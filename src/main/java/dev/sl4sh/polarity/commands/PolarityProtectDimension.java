package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityProtectDimension implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Protects a dimension."))
                .permission("polarity.protectdimension")
                .executor(new PolarityProtectDimension())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());

            if(!worldInfo.isWorldProtected()){

                worldInfo.setDimensionProtected(true);
                Polarity.getPolarity().writeAllConfig();

                caller.sendMessage(Text.of(TextColors.AQUA, "New dimension protected!"));
                return CommandResult.success();

            }

            caller.sendMessage(Text.of(TextColors.AQUA, "This dimension is already protected."));

        }

        return CommandResult.success();

    }

}
