package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.commands.XManagerCommandManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionHelp implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player) {

            printFactionsHelp((Player)src);

        }
        else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }


        return CommandResult.success();

    }

    static public void printFactionsHelp(Player caller){

        caller.sendMessage(Text.of("\u00a72============ Factions Help ============"));
        caller.sendMessage(Text.of("\u00a7a/factions help \u00a7fPrints this help menu."));
        caller.sendMessage(Text.of("\u00a7a/factions join \u00a7fJoins a faction."));
        caller.sendMessage(Text.of("\u00a7a/factions leave \u00a7fLeaves your current faction."));
        caller.sendMessage(Text.of("\u00a7a/factions invite \u00a7fInvites one or several player(s) to your faction."));
        caller.sendMessage(Text.of("\u00a7a/factions create \u00a7fCreates a faction."));
        caller.sendMessage(Text.of("\u00a7a/factions list \u00a7fLists several information about a faction."));

        Optional<XFactionPermissionData> optMbData = XFactionCommandManager.getPlayerFactionPermissions(caller);

        if(!optMbData.isPresent()) { return; }

        caller.sendMessage(Text.of("\u00a7a/factions home \u00a7fTeleport to your faction's home (if set)."));
        caller.sendMessage(Text.of("\u00a7a/factions showclaims \u00a7fShows your faction's claimed chunks. Watch the flames particles!"));

        if(!optMbData.get().getConfigure()) { return; }

        caller.sendMessage(Text.of("\u00a7a/factions claim \u00a7fClaims a chunk for your faction."));
        caller.sendMessage(Text.of("\u00a7a/factions unclaim \u00a7fUnclaims one of your faction's chunks."));
        caller.sendMessage(Text.of("\u00a7a/factions sethome \u00a7fSets your faction's home."));
        caller.sendMessage(Text.of("\u00a7a/factions ally \u00a7fManage alliances for your faction."));
        caller.sendMessage(Text.of("\u00a7a/factions kick \u00a7fKicks a member from your faction."));
        caller.sendMessage(Text.of("\u00a7a/factions perm \u00a7fSet permissions for a member of your faction."));
        caller.sendMessage(Text.of("\u00a7a/factions setdisplayname \u00a7fSets your faction's display name."));
        caller.sendMessage(Text.of("\u00a7a/factions setprefix \u00a7fSets your faction's prefix."));

        Optional<XFaction> optionalXFaction = XFactionCommandManager.getPlayerFaction(caller);

        if(!optionalXFaction.isPresent() || !optionalXFaction.get().isOwner(caller.getName())) { return; }

        caller.sendMessage(Text.of("\u00a7a/factions setowner \u00a7fSet someone from your faction as the new owner (irreversible)."));
        caller.sendMessage(Text.of("\u00a7a/factions disband \u00a7fDisbands your current faction (irreversible)."));

    }

}
