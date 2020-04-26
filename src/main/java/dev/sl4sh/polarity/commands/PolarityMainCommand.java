package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.commands.economy.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class PolarityMainCommand implements CommandExecutor {

    @Nonnull
    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The main Polarity command."))
                .child(PolarityProtectChunk.getCommandSpec(), "protectchunk")
                .child(PolarityUnProtectChunk.getCommandSpec(), "unprotectchunk")
                .child(PolarityProtectDimension.getCommandSpec(), "protectdimension")
                .child(PolarityUnProtectDimension.getCommandSpec(), "unprotectdimension")
                .child(PolarityFreeTransfer.getCommandSpec(), "freetransfer")
                .child(PolarityWarp.getRemoveCommandSpec(), "removewarp")
                .child(PolarityWarp.getSetCommandSpec(), "setwarp")
                .child(PolarityReload.getCommandSpec(), "reload")
                .child(PolarityTeleport.getCommandSpec(), "tp")
                .child(PolarityHelp.getCommandSpec(), "help")
                .child(PolarityTest.getCommandSpec(), "test")
                .child(PolarityAddPositionSnapshot.getCommandSpec(), "addsnap")
                .child(PolaritySummonGameSelection.getCommandSpec(), "gamesnpc")
                .child(PolaritySummonUserShop.getCommandSpec(), "ushopnpc")
                .permission("polarity")
                .executor(new PolarityMainCommand())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        src.sendMessage(Text.of(TextColors.AQUA, "Use subcommands"));
        return CommandResult.success();

    }
}
