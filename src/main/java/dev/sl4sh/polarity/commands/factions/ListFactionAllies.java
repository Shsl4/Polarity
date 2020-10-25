package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.enums.PolarityErrors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ListFactionAllies implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the allies of a faction."))
                .arguments(GenericArguments.optional(new FactionCommandElement(Text.of("factionName"))))
                .permission("polarity.list.factions.allies")
                .executor(new ListFactionAllies())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(args.getOne("factionName").isPresent()){

            listFactionAllies(src, args.getOne("factionName").get().toString().toLowerCase());

        }
        else{

            listFactionAllies(src, "");

        }

        return CommandResult.success();

    }

    private void listFactionAllies(CommandSource src, String targetFactionName){

        if(targetFactionName.equals("")){

            if(src instanceof Player){

                Player caller = (Player)src;

                Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

                if(!optCallerFaction.isPresent()) {  caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

                optCallerFaction.get().listAllies(caller);

            }
            else{

                src.sendMessage(Text.of(TextColors.AQUA, "As you are not a player, you need to specify a faction name"));

            }

        }
        else{

            Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

            if(!optTargetFaction.isPresent()) { src.sendMessage(PolarityErrors.NULLFACTION.getDesc()); return; }

            optTargetFaction.get().listAllies(src);

        }

    }

}
