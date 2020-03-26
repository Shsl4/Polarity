package io.sl4sh.xmanager.commands.economy;

import de.dosmike.sponge.megamenus.MegaMenus;
import de.dosmike.sponge.megamenus.api.MenuRenderer;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import io.sl4sh.xmanager.economy.XEconomyShopRecipe;
import io.sl4sh.xmanager.economy.ui.XButton;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class XEconomyShopTest implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Opens a shop (TEST)."))
                .permission("xmanager.economy.shop")
                .executor(new XEconomyShopTest())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

                BaseMenuImpl menu = MegaMenus.createMenu();
                menu.setTitle(Text.of(TextStyles.BOLD, TextColors.AQUA, "Shop"));
                menu.add(XButton.builder().setPosition(SlotPos.of(0, 0)).setRecipe(new XEconomyShopRecipe(50.0f, ItemStack.builder().itemType(ItemTypes.DIAMOND).quantity(1).build().createSnapshot())).build());
                menu.add(XButton.builder().setPosition(SlotPos.of(0, 1)).setRecipe(new XEconomyShopRecipe(10.0f, ItemStack.builder().itemType(ItemTypes.LOG).quantity(10).build().createSnapshot())).build());
                MenuRenderer render = (MenuRenderer)menu.createGuiRenderer( 3,true);
                render.open(caller);


        }

        return CommandResult.success();

    }
}
