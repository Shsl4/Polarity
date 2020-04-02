package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.commands.elements.XShopCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XShopBuilderRemove implements CommandExecutor
{

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new XShopCommandElement(Text.of("profileName")))
                .permission("xmanager.shopbuilder.edit")
                .executor(new XShopBuilderRemove())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(XManager.getShopProfiles().removeProfileByName((String)args.getOne(Text.of("profileName")).get())){

            src.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Removed your profile named ", (String)args.getOne(Text.of("profileName")).get(), TextColors.AQUA, "."));
            XManager.getXManager().writeShopProfiles();

        }
        else{

            src.sendMessage(Text.of(TextColors.RED, "[ShopBuilder] | Your profile couldn't be removed. It may not exist."));

        }

        return CommandResult.success();

    }
}
