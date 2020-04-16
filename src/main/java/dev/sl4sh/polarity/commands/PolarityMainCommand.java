package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.commands.economy.*;
import dev.sl4sh.polarity.commands.shopbuilder.ShopBuilderMain;
import dev.sl4sh.polarity.commands.spleef.XSpleefMain;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PolarityMainCommand implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main Polarity command."))
                .child(PolarityProtectChunk.getCommandSpec(), "protectchunk")
                .child(PolarityUnProtectChunk.getCommandSpec(), "unprotectchunk")
                .child(ShopBuilderMain.getCommandSpec(), "shopbuilder")
                .child(PolarityReload.getCommandSpec(), "reload")
                .child(PolarityProtectDimension.getCommandSpec(), "protectdimension")
                .child(PolarityUnProtectDimension.getCommandSpec(), "unprotectdimension")
                .child(PolarityWarp.getRemoveCommandSpec(), "removewarp")
                .child(PolarityWarp.getSetCommandSpec(), "setwarp")
                .child(PolarityTeleport.getCommandSpec(), "tp")
                .child(XSpleefMain.getCommandSpec(), "spleef")
                .child(PolarityPlayerTransfer.getCommandSpec(), "playertransfer")
                .child(PolarityFactionTransfer.getCommandSpec(), "factiontransfer")
                .child(PolarityAdminTransfer.getCommandSpec(), "admintransfer")
                .child(PolarityShowBalance.getCommandSpec(), "showbalance")
                .child(PolarityHelp.getCommandSpec(), "help")
                .permission("polarity")
                .executor(new PolarityMainCommand())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        PolarityHelp.printEconomyHelp(src);
        return CommandResult.success();

    }
}
