package dev.sl4sh.polarity.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

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
        printHelp(src);
        return CommandResult.success();
    }

    private void printHelp(CommandSource source){

        TextColor helpAccentColor = TextColors.GREEN;

        source.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Party Help ============"));
        source.sendMessage(Text.of(helpAccentColor, "/party ", TextColors.WHITE, "Prints this help menu."));
        source.sendMessage(Text.of(helpAccentColor, "/party invite <Player> ", TextColors.WHITE, "Invites a player to your party."));
        source.sendMessage(Text.of(helpAccentColor, "/party kick <Player> ", TextColors.WHITE, "Kicks someone out of your party."));
        source.sendMessage(Text.of(helpAccentColor, "/party leave <Player> ", TextColors.WHITE, "Leaves your current party."));

    }

}
