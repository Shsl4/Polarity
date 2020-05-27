package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class PolaritySummonGameSelection implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons an NPC which allow players to pick a game."))
                .arguments(GenericArguments.integer(Text.of("GameID")), GenericArguments.integer(Text.of("PageID")))
                .permission("polarity.summongameselection")
                .executor(new PolaritySummonGameSelection())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player caller = (Player)src;

        Polarity.getNPCManager().makeGameSelectionNPC(caller.getLocation(), (Integer)args.getOne("GameID").get(), (Integer)args.getOne("PageID").get());

        return CommandResult.success();

    }
}
