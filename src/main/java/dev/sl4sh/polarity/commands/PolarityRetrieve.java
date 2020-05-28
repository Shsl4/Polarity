package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.InventoryBackup;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class PolarityRetrieve implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()

                .description(Text.of("Retrieves backed up items."))
                .permission("polarity.retrieve")
                .executor(new PolarityRetrieve())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { throw new CommandException(Text.of("This is a player only command.")); }

        Player caller = (Player)src;
        Optional<InventoryBackup> optBackup = Polarity.getInventoryBackups().getBackupForPlayer(caller.getUniqueId());

        if(!Utilities.getOrCreateWorldInfo(caller.getWorld()).isGameWorld()){

            if(optBackup.isPresent()){

                if(optBackup.get().getSnapshots().size() > 0){

                    caller.sendMessage(Text.of(TextColors.AQUA, "Retrieved ", optBackup.get().getSnapshots().size(), " stacks."));
                    Utilities.restorePlayerInventory(caller);
                    return CommandResult.success();

                }

            }

            caller.sendMessage(Text.of(TextColors.RED, "No items to retrieve."));

        }
        else{

            caller.sendMessage(Text.of(TextColors.RED, "You may only retrieve your items outside a game world."));

        }

        return CommandResult.success();

    }
}
