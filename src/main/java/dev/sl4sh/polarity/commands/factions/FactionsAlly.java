package dev.sl4sh.polarity.commands.factions;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class FactionsAlly implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Factions alliance command. Prints help if no argument is provided."))
                .permission("polarity.factions.ally")
                .child(FactionsAllyRequest.getCommandSpec(), "request")
                .child(FactionsAllyAccept.getCommandSpec(), "accept")
                .child(FactionsAllyDecline.getCommandSpec(), "decline")
                .executor(new FactionsAlly())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        printAllianceHelp(src);

        return CommandResult.success();

    }

    static public void printAllianceHelp(CommandSource src){

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions Alliance Help ============"));
        src.sendMessage(Text.of(listTintColor, "/factions ally request ", TextColors.WHITE, "Requests an alliance to another faction."));
        src.sendMessage(Text.of(listTintColor, "/factions ally accept ", TextColors.WHITE, "Accept an alliance from another faction."));
        src.sendMessage(Text.of(listTintColor, "/factions ally decline ", TextColors.WHITE, "Declines an alliance from another faction."));

    }

}
