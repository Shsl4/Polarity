package dev.sl4sh.polarity.commands;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

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

        ItemStack stack = ItemStack.builder().itemType(ItemTypes.DIAMOND_SWORD).quantity(1).build();
        stack.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Coolest Sword"));
        stack.offer(Keys.ITEM_DURABILITY, 50);
        stack.offer(Keys.ITEM_ENCHANTMENTS, Collections.singletonList(Enchantment.builder().level(5).type(EnchantmentTypes.SHARPNESS).build()));

        @Nonnull
        final HoconConfigurationLoader confLoader = HoconConfigurationLoader.builder().setFile(new File("Polarity/data/Test.hocon")).build();

        try {
            confLoader.save(confLoader.createEmptyNode().setValue(TypeToken.of(ItemStackSnapshot.class), stack.createSnapshot()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        /*TabListManager.refreshAll();

        player.sendMessage(Text.of(TextColors.AQUA, "Refreshed."));*/

        return CommandResult.success();

    }
}
