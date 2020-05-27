package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.economy.PolarityAccount;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.TabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactionsForceDisband implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Force disband a faction."))
                .permission("polarity.factions.forcedisband")
                .arguments(GenericArguments.onlyOne(new FactionCommandElement(Text.of("factionName"))))
                .executor(new FactionsForceDisband())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof ConsoleSource) {

            ConsoleSource serv = (ConsoleSource)src;

            if(forceDisbandFaction(args.getOne("factionName").get().toString().toLowerCase())){

                serv.sendMessage(Text.of(TextColors.GREEN, "The faction " , args.getOne("factionName").get().toString().toLowerCase() , " has been successfully disbanded."));
                TabListManager.refreshAll();

            }
            else{

                serv.sendMessage(Text.of(TextColors.RED, "Failed to disband the faction. Does it exists?"));

            }


        }
        else{

            src.sendMessage(PolarityErrors.SERVERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private Boolean forceDisbandFaction(String factionName){

        List<Faction> factions = Polarity.getFactions().getList();

        Optional<Faction> optPendingDeleteFaction = Utilities.getFactionByName(factionName);

        if(optPendingDeleteFaction.isPresent()){

            Faction pendingDeleteFaction = optPendingDeleteFaction.get();
            List<Player> players = new ArrayList<>();

            for(FactionMemberData memberData : pendingDeleteFaction.getMemberDataList()){

                if(Utilities.getPlayerByUniqueID(memberData.getPlayerUniqueID()).isPresent()){

                    players.add(Utilities.getPlayerByUniqueID(memberData.getPlayerUniqueID()).get());

                }

            }

            if(Polarity.getEconomyService().isPresent()){

                Polarity.getAccounts().remove((PolarityAccount)Polarity.getEconomyService().get().getOrCreateAccount(pendingDeleteFaction.getUniqueId()).get());

            }

            Polarity.getFactions().remove(pendingDeleteFaction);
            factions.remove(pendingDeleteFaction);
            Polarity.getPolarity().writeAllConfig();

            for(Player ply : players){

                ply.sendMessage(Text.of(TextColors.RED, "\u00a7lYour faction has been disbanded by an administrator."));
                ply.playSound(SoundTypes.AMBIENT_CAVE, ply.getPosition(), .25);

            }

            return true;

        }

        return false;

    }

}
