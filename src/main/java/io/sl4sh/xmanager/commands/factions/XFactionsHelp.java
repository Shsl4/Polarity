package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XFactionsHelp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Prints help about X factions."))
                .permission("xmanager.factions.help")
                .executor(new XFactionsHelp())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if(src instanceof Player) {

            printFactionsHelp((Player)src);

        }
        else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }


        return CommandResult.success();

    }

    static public void printFactionsHelp(Player caller){

        TextColor helpAccentColor = TextColors.GREEN;

        caller.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions Help ============"));
        caller.sendMessage(Text.of(helpAccentColor, "/factions help ", TextColors.WHITE, "Prints this help menu."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions join ", TextColors.WHITE, "Joins a faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions leave ", TextColors.WHITE, "Leaves your current faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions invite ", TextColors.WHITE, "Invites one or several player(s) to your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions create ", TextColors.WHITE, "Creates a faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions list ", TextColors.WHITE, "Lists several information about a faction."));

        Optional<XFactionPermissionData> optMbData = XUtilities.getPlayerFactionPermissions(caller);

        if(!optMbData.isPresent()) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions home ", TextColors.WHITE, "Teleport to your faction's home (if set)."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions showclaims ", TextColors.WHITE, "Shows your faction's claimed chunks. Watch the flames particles!"));

        if(!optMbData.get().getManage()) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions claim ", TextColors.WHITE, "Claims a chunk for your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions unclaim ", TextColors.WHITE, "Unclaims one of your faction's chunks."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions sethome ", TextColors.WHITE, "Sets your faction's home."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions ally ", TextColors.WHITE, "Manage alliances for your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions kick ", TextColors.WHITE, "Kicks a member from your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setperm ", TextColors.WHITE, "Sets permissions for a member of your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setdisplayname ", TextColors.WHITE, "Sets your faction's display name."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setprefix ", TextColors.WHITE, "Sets your faction's prefix."));

        Optional<XFaction> optionalXFaction = XUtilities.getPlayerFaction(caller);

        if(!optionalXFaction.isPresent() || !optionalXFaction.get().isOwner(caller.getName())) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions setowner ", TextColors.WHITE, "Set someone from your faction as the new owner (irreversible)."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions disband ", TextColors.WHITE, "Disbands your current faction (irreversible)."));

    }

}
