package dev.sl4sh.polarity.commands.debug;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class TabRemove implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Test command for experimental dev."))
                .arguments(GenericArguments.player(Text.of("target")))
                .permission("polarity.tabremove")
                .executor(new TabRemove())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player player = (Player)src;
            Player target = (Player)args.getOne("target").get();

            target.offer(Keys.VANISH, true);

            player.sendMessage(Text.of(TextColors.AQUA, (player.getTabList().removeEntry(target.getUniqueId()).isPresent() ? "Success" : "Fail")));

        }

        return CommandResult.success();

    }
}
