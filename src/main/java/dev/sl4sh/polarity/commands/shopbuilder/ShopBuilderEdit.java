package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.shops.admin.ManageAdminShopUI;
import dev.sl4sh.polarity.commands.elements.ShopCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ShopBuilderEdit implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Edits a shop profile by its name"))
                .arguments(new ShopCommandElement(Text.of("profileName")))
                .permission("polarity.shopbuilder.edit")
                .executor(new ShopBuilderEdit())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            if(!Polarity.getShopProfiles().getExistingShopProfilesNames().contains((String)args.getOne("profileName").get())) { throw new CommandException(Text.of("This shop profile does not exist")); }

            new ManageAdminShopUI(caller, (String)args.getOne("profileName").get()).open();

        }

        return CommandResult.success();

    }
}
