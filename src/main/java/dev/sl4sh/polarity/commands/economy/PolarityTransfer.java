package dev.sl4sh.polarity.commands.economy;

import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityTransfer implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Transfers money to a target."))
                .child(PolarityPlayerTransfer.getCommandSpec(), "player")
                .child(PolarityFactionTransfer.getCommandSpec(), "faction")
                .permission("polarity.transfer")
                .executor(new PolarityTransfer())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.RED, "You need to specify a target : faction or player."));
        return CommandResult.success();

    }

}
