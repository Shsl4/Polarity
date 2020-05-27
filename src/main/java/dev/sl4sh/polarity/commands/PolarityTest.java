package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.TabListManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityTest implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Test command for experimental dev."))
                .permission("polarity.test")
                .executor(new PolarityTest())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player player = (Player)src;

        /*List<UUID> pendingRemove = new ArrayList<>();

        for(WorldInfo info : Polarity.getWorldsInfo().getList()){

            if(!Sponge.getServer().getWorldProperties(info.getWorldUniqueID()).isPresent()){

                pendingRemove.add(info.getWorldUniqueID());

            }
        }

        for(UUID id : pendingRemove){

            Polarity.getWorldsInfo().removeWorldInfo(id);

        }*/

        TabListManager.refreshAll();

        player.sendMessage(Text.of(TextColors.AQUA, "Refreshed."));

        return CommandResult.success();

    }
}
