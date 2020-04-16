package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.commands.elements.ShopCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ShopBuilderRemove implements CommandExecutor
{

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main ShopBuilder command"))
                .arguments(new ShopCommandElement(Text.of("profileName")))
                .permission("polarity.shopbuilder.remove")
                .executor(new ShopBuilderRemove())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(Polarity.getShopProfiles().removeProfileByName((String)args.getOne(Text.of("profileName")).get())){

            src.sendMessage(Text.of(TextColors.AQUA, "[ShopBuilder] | Removed your profile named ", (String)args.getOne(Text.of("profileName")).get(), TextColors.AQUA, "."));
            Polarity.getPolarity().writeAllConfig();

        }
        else{

            src.sendMessage(Text.of(TextColors.RED, "[ShopBuilder] | Your profile couldn't be removed. It may not exist."));

        }

        return CommandResult.success();

    }
}
