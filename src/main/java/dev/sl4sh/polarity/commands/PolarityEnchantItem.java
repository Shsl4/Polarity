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
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class PolarityEnchantItem implements CommandExecutor {

    static Map<String, EnchantmentType> getAllEnchantments(){

        Map<String, EnchantmentType> types = new HashMap<>();

        for(EnchantmentType type : Sponge.getRegistry().getAllOf(EnchantmentType.class)){

            types.put(type.getId(), type);

        }

        return types;

    }

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()

                .description(Text.of("Enchants an item."))
                .arguments(GenericArguments.choices(Text.of("enchantment"),getAllEnchantments()), GenericArguments.integer(Text.of("level")))
                .permission("polarity.enchantitem")
                .executor(new PolarityEnchantItem())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player caller = (Player)src;

        if(caller.getItemInHand(HandTypes.MAIN_HAND).isPresent()){

            EnchantmentType type = (EnchantmentType)args.getOne("enchantment").get();
            Integer level = (Integer)args.getOne("level").get();

            List<Enchantment> enchants = caller.getItemInHand(HandTypes.MAIN_HAND).get().get(Keys.ITEM_ENCHANTMENTS).orElse(new ArrayList<>());

            enchants.add(Enchantment.builder().type(type).level(level).build());

            caller.getItemInHand(HandTypes.MAIN_HAND).get().offer(Keys.ITEM_ENCHANTMENTS, enchants);

            return CommandResult.success();

        }

        caller.sendMessage(Text.of(TextColors.RED, "No item in hand"));

        return CommandResult.success();

    }

}