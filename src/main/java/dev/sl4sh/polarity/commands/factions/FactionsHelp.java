package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
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

public class FactionsHelp implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Prints help about factions."))
                .permission("polarity.factions.help")
                .executor(new FactionsHelp())
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

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }


        return CommandResult.success();

    }

    static public void printFactionsHelp(Player caller){

        TextColor helpAccentColor = TextColors.GREEN;
        PolarityCurrency dollarCurrency = new PolarityCurrency();

        caller.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions Help ============"));
        caller.sendMessage(Text.of(helpAccentColor, "/factions help ", TextColors.WHITE, "Prints this help menu."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions leave ", TextColors.WHITE, "Leaves your current faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions invite ", TextColors.WHITE, "Invites one or several player(s) to your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions create ", TextColors.WHITE, "Creates a faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions list ", TextColors.WHITE, "Lists several information about a faction."));

        Optional<FactionPermissionData> optMbData = Utilities.getPlayerFactionPermissions(caller);

        if(!optMbData.isPresent()) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions home ", TextColors.WHITE, "Teleport to your faction's home (if set)."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions showclaims ", TextColors.WHITE, "Shows your faction's claimed chunks. Watch the flames particles!"));

        if(!optMbData.get().getManage()) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions claim ", TextColors.WHITE, "Claims a chunk for your faction. (Costs money)"));
        caller.sendMessage(Text.of(helpAccentColor, "/factions unclaim ", TextColors.WHITE, "Unclaims one of your faction's chunks."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions sethome ", TextColors.WHITE, "Sets your faction's home."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions ally ", TextColors.WHITE, "Manage alliances for your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions kick ", TextColors.WHITE, "Kicks a member from your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setperm ", TextColors.WHITE, "Sets permissions for a member of your faction."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setdisplayname ", TextColors.WHITE, "Sets your faction's display name."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions setprefix ", TextColors.WHITE, "Sets your faction's prefix."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions pay ", TextColors.WHITE, "Pays a member of you faction with your faction's account."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions withdraw ", TextColors.WHITE, "Withdraws money from your faction's account"));
        caller.sendMessage(Text.of(helpAccentColor, "/factions showbalance ", TextColors.WHITE, "Shows your faction's account current balance"));

        Optional<Faction> optionalFaction = Utilities.getPlayerFaction(caller);

        if(!optionalFaction.isPresent() || !optionalFaction.get().isOwner(caller)) { return; }

        caller.sendMessage(Text.of(helpAccentColor, "/factions setowner ", TextColors.WHITE, "Set someone from your faction as the new owner (irreversible)."));
        caller.sendMessage(Text.of(helpAccentColor, "/factions disband ", TextColors.WHITE, "Disbands your current faction (irreversible). The bank account of your faction will be gone too."));

    }

}
