package io.sl4sh.xmanager.commands.trade;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XTradeProfile;
import io.sl4sh.xmanager.economy.merchants.XHuman;
import io.sl4sh.xmanager.economy.merchants.XVillager;
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

public class XTradeBuilderSummon implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons a villager with the selected profile name."))
                .arguments(GenericArguments.string(Text.of("profileName")))
                .permission("xmanager.tradebuilder.summon")
                .executor(new XTradeBuilderSummon())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            String profileName = (String)args.getOne("profileName").get();

            Optional<XTradeProfile> optionalXTradeProfile = XManager.getTradeProfiles().getTradeProfileByName(profileName);

            if(!optionalXTradeProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[ShopBuilder] | This profile name does not exist.")); return CommandResult.success(); }

            XVillager.summonMerchant(caller.getWorld(), caller, profileName);

        }

        return CommandResult.success();

    }
}
