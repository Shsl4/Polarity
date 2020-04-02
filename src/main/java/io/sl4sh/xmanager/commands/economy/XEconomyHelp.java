package io.sl4sh.xmanager.commands.economy;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class XEconomyHelp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Prints the help menu."))
                .permission("xmanager.economy.help")
                .executor(new XEconomyHelp())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        printEconomyHelp(src);
        return CommandResult.success();

    }

    public static void printEconomyHelp(CommandSource caller){

        TextColor helpAccentColor = TextColors.GREEN;

        caller.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Economy Help ============"));
        caller.sendMessage(Text.of(helpAccentColor, "/economy (help) ", TextColors.WHITE, "Prints this help menu."));
        caller.sendMessage(Text.of(helpAccentColor, "/economy transfer ", TextColors.WHITE, "Transfers money to a player."));
        caller.sendMessage(Text.of(helpAccentColor, "/economy showbalance ", TextColors.WHITE, "Shows your current balance."));
        caller.sendMessage(Text.of(helpAccentColor, "/economy factiontransfer ", TextColors.WHITE, "Transfers money to a faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/economy playertransfer ", TextColors.WHITE, "Transfers money to a player. (The player must be online)"));


    }

}
