package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.enums.PolarityErrors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class FactionsMainCommand implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Factions command. Prints help if no argument is provided."))
                .permission("polarity.factions")
                .child(FactionsCreate.getCommandSpec(), "create")
                .child(FactionsHelp.getCommandSpec(), "help")
                .child(FactionsClaim.getCommandSpec(), "claim")
                .child(FactionsForceDisband.getCommandSpec(), "forcedisband")
                .child(FactionsDisband.getCommandSpec(), "disband")
                .child(FactionsLeave.getCommandSpec(), "leave")
                .child(FactionsUnClaim.getCommandSpec(), "unclaim")
                .child(FactionsList.getCommandSpec(), "list")
                .child(FactionsSetDisplayName.getCommandSpec(), "setdisplayname")
                .child(FactionsSetPrefix.getCommandSpec(), "setprefix")
                .child(FactionsSetPerm.getCommandSpec(), "setperm")
                .child(FactionsInvite.getCommandSpec(), "invite")
                .child(FactionsJoin.getCommandSpec(), "join")
                .child(FactionsKick.getCommandSpec(), "kick")
                .child(FactionsShowClaims.getCommandSpec(), "showclaims")
                .child(FactionsHome.getCommandSpec(), "home")
                .child(FactionsSetHome.getCommandSpec(), "sethome")
                .child(FactionsSetOwner.getCommandSpec(), "setowner")
                .child(FactionsAlly.getCommandSpec(), "ally")
                .child(FactionsDeAlly.getCommandSpec(), "deally")
                .child(FactionsWithdraw.getCommandSpec(), "withdraw")
                .child(FactionsPay.getCommandSpec(), "pay")
                .child(FactionsShowBalance.getCommandSpec(), "showbalance")
                .executor(new FactionsMainCommand())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player) {

            FactionsHelp.printFactionsHelp((Player)src);

        }
        else {

            src.sendMessage(Text.of(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc()));

        }


        return CommandResult.success();

    }

}


