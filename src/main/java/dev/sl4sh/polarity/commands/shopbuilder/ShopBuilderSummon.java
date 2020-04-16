package dev.sl4sh.polarity.commands.shopbuilder;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.commands.elements.ShopCommandElement;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.shops.merchants.ShopNPC;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ShopBuilderSummon implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Summons a merchant with the specified parameters."))
                .arguments(new ShopCommandElement(Text.of("profileName")))
                .permission("polarity.shopbuilder.summon")
                .executor(new ShopBuilderSummon())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            String profileName = (String)args.getOne("profileName").get();

            Optional<ShopProfile> optionalShopProfile = Polarity.getShopProfiles().getShopProfileByName(profileName);

            if(!optionalShopProfile.isPresent()) { caller.sendMessage(Text.of(TextColors.RED, "[ShopBuilder] | This profile name does not exist.")); return CommandResult.success(); }

            ShopNPC.summonNPC(caller.getWorld(), caller, profileName);

        }

        return CommandResult.success();

    }

}
