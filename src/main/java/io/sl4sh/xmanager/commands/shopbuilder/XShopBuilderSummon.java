package io.sl4sh.xmanager.commands.shopbuilder;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.merchants.XHuman;
import io.sl4sh.xmanager.economy.XShopProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XShopBuilderSummon implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons a merchant with the specified parameters."))
                .arguments(GenericArguments.string(Text.of("profileName")), GenericArguments.optional(GenericArguments.player(Text.of("playerSkin"))))
                .permission("xmanager.shopbuilder.summon")
                .executor(new XShopBuilderSummon())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {


        if(src instanceof Player){

            Player caller = (Player)src;

            String profileName = (String)args.getOne("profileName").get();
            Optional<Player> playerSkin = args.getOne("playerSkin");

            Optional<XShopProfile> optionalXShopProfile = XManager.getShopProfiles().getShopProfileByName(profileName);

            if(!optionalXShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[ShopBuilder] | This profile name does not exist.")); return CommandResult.success(); }

            XHuman.summonMerchant(caller.getWorld(), caller, profileName, playerSkin);

        }

        return CommandResult.success();

    }
}
