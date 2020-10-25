package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.concurrent.atomic.AtomicBoolean;

public class PolarityPartyInvite implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Invite your friends in your party."))
                .arguments(GenericArguments.player(Text.of("player")))
                .permission("polarity.party.invite")
                .executor(new PolarityPartyInvite())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!args.getOne(Text.of("player")).isPresent()){

                caller.sendMessage(Text.of(TextColors.AQUA, "The specified player does not exist."));
                return CommandResult.success();

            }

            Player target = (Player)args.getOne(Text.of("player")).get();

            if(caller.equals(target)) { caller.sendMessage(Text.of(TextColors.AQUA, "You can't invite yourself to your party.")); return CommandResult.success(); }

            if(Polarity.getPartyManager().getPlayerParty(target).isPresent()) { caller.sendMessage(Text.of(TextColors.AQUA, "This player is already in a party.")); return CommandResult.success();}

            caller.sendMessage(Text.of(TextColors.AQUA, "Successfully invited ", TextColors.LIGHT_PURPLE, target.getName(), TextColors.AQUA, " to your party"));
            caller.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.25);

            AtomicBoolean executed = new AtomicBoolean(false);

            Text button = Text.builder().onClick(TextActions.executeCallback((source) -> {

                if(!executed.get()){

                    Polarity.getPartyManager().joinPlayerParty(target, caller);
                    executed.set(true);

                }
                else{

                    source.sendMessage(Text.of(TextColors.AQUA, "This request has already been processed"));

                }

            })).append(Text.of(TextStyles.UNDERLINE, TextColors.GOLD, "Click here")).build();

            target.sendMessage(Text.of(TextColors.LIGHT_PURPLE, caller.getName(), TextColors.AQUA, " invited you to a party. ", button, TextColors.RESET, TextColors.AQUA, " to join it."));
            target.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, caller.getPosition(), 0.25);

        }

        return CommandResult.success();

    }

}
