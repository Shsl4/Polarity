package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import dev.sl4sh.polarity.enums.PolarityStyles;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityRenameItem implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()

                .description(Text.of("Applies a text color and style to the item held in the main hand."))
                .arguments(GenericArguments.string(Text.of("value")))
                .permission("polarity.renameitem")
                .executor(new PolarityRenameItem())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player caller = (Player)src;

        if(caller.getItemInHand(HandTypes.MAIN_HAND).isPresent()){

            String str = ((String)args.getOne("value").get()).replace("&", "\u00a7");

            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.DISPLAY_NAME, Text.of(str));

            return CommandResult.success();

        }

        caller.sendMessage(Text.of(TextColors.RED, "No item in hand"));

        return CommandResult.success();

    }

}
