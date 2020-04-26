package dev.sl4sh.polarity.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collections;

public class PolarityCosmeticEnchant implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()

                .description(Text.of("Enchants an item for display (hides meta)."))
                .permission("polarity.cosmeticenchant")
                .executor(new PolarityCosmeticEnchant())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player caller = (Player)src;

        if(caller.getItemInHand(HandTypes.MAIN_HAND).isPresent()){

            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.ITEM_ENCHANTMENTS, Collections.singletonList(Enchantment.builder().type(EnchantmentTypes.EFFICIENCY).level(1).build()));
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_ENCHANTMENTS, true);
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_ATTRIBUTES, true);
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_MISCELLANEOUS, true);
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_UNBREAKABLE, true);
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_CAN_DESTROY, true);
            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.HIDE_CAN_PLACE, true);

            return CommandResult.success();

        }

        caller.sendMessage(Text.of(TextColors.RED, "No item in hand"));

        return CommandResult.success();

    }
}
