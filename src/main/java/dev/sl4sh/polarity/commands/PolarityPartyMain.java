package dev.sl4sh.polarity.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class PolarityPartyMain implements CommandExecutor {


    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main party command."))
                .permission("polarity.party")
                .child(PolarityPartyInvite.getCommandSpec(), "invite")
                .child(PolarityPartyKick.getCommandSpec(), "kick")
                .child(PolarityPartyLeave.getCommandSpec(), "leave")
                .executor(new PolarityPartyMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.success();
    }
}
