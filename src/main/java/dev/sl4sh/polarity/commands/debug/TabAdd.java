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
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class TabAdd implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Test command for experimental dev."))
                .arguments(GenericArguments.player(Text.of("target")))
                .permission("polarity.tabadd")
                .executor(new TabAdd())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player player = (Player)src;
            Player target = (Player)args.getOne("target").get();

            try{

               player.getTabList().addEntry(TabListEntry.builder()
                       .gameMode(target.get(Keys.GAME_MODE).get())
                       .displayName(Text.of(target.getName()))
                       .latency(target.getConnection().getLatency())
                       .list(player.getTabList())
                       .profile(target.getProfile())
                       .build());

               player.sendMessage(Text.of(TextColors.GREEN, "Success"));


            }
            catch(Throwable e){

                player.sendMessage(Text.of(TextColors.RED, "Fail"));
                e.printStackTrace();

            }


        }

        return CommandResult.success();

    }
}
