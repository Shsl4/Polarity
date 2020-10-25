package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.commands.factions.ListFactions;
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

public class PolarityList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists existing listing types."))
                .permission("polarity.list")
                .child(ListFactions.getCommandSpec(), "factions")
                .child(PolarityWarp.getListCommandSpec(), "warps")
                .executor(new PolarityList())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Listing Help ============"));
        src.sendMessage(Text.of(listTintColor, "/list factions ", TextColors.WHITE, "Lists existing factions."));
        src.sendMessage(Text.of(listTintColor, "/list warps ", TextColors.WHITE, "Lists existing warps."));

        return CommandResult.success();

    }




}
