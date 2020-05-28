package dev.sl4sh.polarity.commands;

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

public class PolarityUUID implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Prints your UUID."))
                .permission("polarity.uuid")
                .executor(new PolarityUUID())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { throw new CommandException(Text.of("This is a player only command.")); }

        Player caller = (Player) src;

        caller.sendMessage(Text.of(TextColors.AQUA, "Your Player UUID is ", caller.getUniqueId(), "."));

        return CommandResult.success();

    }


}
