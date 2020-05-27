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

public class PolarityPartyKick implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Kicks a player from your party."))
                .arguments(GenericArguments.player(Text.of("player")))
                .permission("polarity.party.kick")
                .executor(new PolarityPartyKick())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;
            Player target = (Player)args.getOne(Text.of("player")).get();

            if(Polarity.getPartyManager().getPlayerParty(caller).isPresent() && caller.getUniqueId().equals(target.getUniqueId())){

                Text leave = Text.builder().onClick(TextActions.executeCallback((source) -> {

                    caller.playSound(SoundTypes.UI_BUTTON_CLICK, caller.getPosition(), 0.25);

                    try {

                        new PolarityPartyLeave().execute(src, args);

                    } catch (CommandException e) {

                        caller.sendMessage(e.getText());

                    }


                })).append(Text.of(TextStyles.UNDERLINE, TextColors.RED, "Leave")).build();

                caller.sendMessage(Text.of(TextColors.AQUA, "You can't kick yourself out of your party. ", leave, TextStyles.RESET, TextColors.AQUA, " instead."));

                return CommandResult.success();

            }

            Polarity.getPartyManager().kickFromParty(caller, target);

        }

        return CommandResult.success();

    }

}
