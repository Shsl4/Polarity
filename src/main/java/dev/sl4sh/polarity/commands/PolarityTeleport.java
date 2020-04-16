package dev.sl4sh.polarity.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityTeleport implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleports a player to another. Works through dimensions."))
                .arguments(GenericArguments.player(Text.of("firstPlayer")), GenericArguments.optional(GenericArguments.player(Text.of("secondPlayer"))))
                .permission("polarity.tp")
                .executor(new PolarityTeleport())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player first = (Player)args.getOne("firstPlayer").get();

        if(args.getOne("secondPlayer").isPresent()){

            Player second = (Player)args.getOne("secondPlayer").get();
            first.setLocation(second.getLocation());
            return CommandResult.empty();

        }

        if(src instanceof Player){

            Player caller = (Player)src;
            caller.setLocation(first.getLocation());

        }
        else{

            src.sendMessage(Text.of(TextColors.RED, "Needs a second argument"));

        }

        return CommandResult.empty();

    }

}
