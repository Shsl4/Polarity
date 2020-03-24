package io.sl4sh.xmanager.commands.factions;

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

public class XFactionsListHelp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("List information about factions."))
                .permission("xmanager.factions.list.help")
                .executor(new XFactionsListHelp())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        printXFactionListHelp(src);

        return CommandResult.success();

    }

    private void printXFactionListHelp(CommandSource src){

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions listing Help ============"));
        src.sendMessage(Text.of(listTintColor, "/factions list ", TextColors.WHITE, "Lists all existing factions."));
        src.sendMessage(Text.of(listTintColor, "/factions list help ", TextColors.WHITE, "Shows this help page."));
        src.sendMessage(Text.of(listTintColor, "/factions list members <factionName> ", TextColors.WHITE, "Lists all members of a faction."));
        src.sendMessage(Text.of(listTintColor, "/factions list allies <factionName> ", TextColors.WHITE, "Lists all allied factions of a faction."));

    }
}
