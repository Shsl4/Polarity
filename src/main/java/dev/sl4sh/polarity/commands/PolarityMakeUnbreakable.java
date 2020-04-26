package dev.sl4sh.polarity.commands;

import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolarityMakeUnbreakable implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()

                .description(Text.of("Enchants an item."))
                .arguments(GenericArguments.bool(Text.of("value")))
                .permission("polarity.makeunbreakable")
                .executor(new PolarityMakeUnbreakable())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player caller = (Player)src;

        if(caller.getItemInHand(HandTypes.MAIN_HAND).isPresent()){

            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.UNBREAKABLE, (Boolean) args.getOne("value").get());

            return CommandResult.success();

        }

        caller.sendMessage(Text.of(TextColors.RED, "No item in hand"));

        return CommandResult.success();

    }

}
