package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.enums.PolarityErrors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityUnProtectDimension implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Removes a protected dimension."))
                .permission("polarity.unprotectdimension")
                .executor(new PolarityUnProtectDimension())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;
            unProtectDimension(ply);

        } else {

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void unProtectDimension(Player caller){

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());

        // If the chunk is protected
        if(worldInfo.isWorldProtected()){

            // Remove the protected chunk and save the configuration.
           worldInfo.setDimensionProtected(false);
           Polarity.getPolarity().writeAllConfig();
           caller.sendMessage(Text.of(TextColors.GREEN, "Removed a protected dimension: ", worldInfo.getTargetWorld().get().getName()));

        }
        // else print a message
        else{

            caller.sendMessage(Text.of(TextColors.AQUA, "This dimension is not protected."));

        }

    }

}
