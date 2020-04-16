package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityReload implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Reloads all of Polarity's configuration files."))
                .permission("polarity.reload")
                .executor(new PolarityReload())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Polarity.getPolarity().loadAllConfig();

        src.sendMessage(Text.of(TextColors.AQUA, "[Polarity] | Reloaded configuration."));

        return CommandResult.success();

    }
}
