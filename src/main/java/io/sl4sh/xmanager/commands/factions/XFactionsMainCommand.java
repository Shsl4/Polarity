package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XFactionsMainCommand implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("XFactions command. Prints help if no argument is provided."))
                .permission("xmanager.factions")
                .child(XFactionsCreate.getCommandSpec(), "create")
                .child(XFactionsHelp.getCommandSpec(), "help")
                .child(XFactionsClaim.getCommandSpec(), "claim")
                .child(XFactionsForceDisband.getCommandSpec(), "forcedisband")
                .child(XFactionsDisband.getCommandSpec(), "disband")
                .child(XFactionsLeave.getCommandSpec(), "leave")
                .child(XFactionsUnClaim.getCommandSpec(), "unclaim")
                .child(XFactionsList.getCommandSpec(), "list")
                .child(XFactionsSetDisplayName.getCommandSpec(), "setdisplayname")
                .child(XFactionsSetPrefix.getCommandSpec(), "setprefix")
                .child(XFactionsSetPerm.getCommandSpec(), "setperm")
                .child(XFactionsInvite.getCommandSpec(), "invite")
                .child(XFactionsJoin.getCommandSpec(), "join")
                .child(XFactionsKick.getCommandSpec(), "kick")
                .child(XFactionsShowClaims.getCommandSpec(), "showclaims")
                .child(XFactionsHome.getCommandSpec(), "home")
                .child(XFactionsSetHome.getCommandSpec(), "sethome")
                .child(XFactionsSetOwner.getCommandSpec(), "setowner")
                .child(XFactionsAlly.getCommandSpec(), "ally")
                .child(XFactionsDeAlly.getCommandSpec(), "deally")
                .child(XFactionsWithdraw.getCommandSpec(), "withdraw")
                .child(XFactionsPay.getCommandSpec(), "pay")
                .child(XFactionsShowBalance.getCommandSpec(), "showbalance")
                .executor(new XFactionsMainCommand())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player) {

            XFactionsHelp.printFactionsHelp((Player)src);

        }
        else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }


        return CommandResult.success();

    }

}


