package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class FactionsList implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the existing factions."))
                .permission("polarity.factions.list")
                .child(FactionsListAllies.getCommandSpec(), "allies")
                .child(FactionsListMembers.getCommandSpec(), "members")
                .child(FactionsListHelp.getCommandSpec(), "help")
                .executor(new FactionsList())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        listFactions(src);

        return CommandResult.success();

    }

    private void listFactions(CommandSource src){

        TextColor listTintColor = TextColors.GREEN;

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Factions list ============"));

        List<Faction> factionList = Polarity.getFactions().getList();

        if(factionList.size() <= 0){

            src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!"));

        }
        else{

            int it = 1;

            for(Faction faction : factionList){

                src.sendMessage(Text.of(listTintColor , "#" , it , ". " , TextColors.WHITE , faction.getDisplayName(), listTintColor , " | Owner: " , TextColors.WHITE , (Utilities.getPlayerByUniqueID(faction.getOwner()).isPresent() ? Utilities.getPlayerByUniqueID(faction.getOwner()).get().getName() : "Unknown") , listTintColor , " | Real name: " , TextColors.WHITE , faction.getName()));
                it++;

            }

        }

    }

}
