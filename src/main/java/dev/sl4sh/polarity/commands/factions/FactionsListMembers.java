package dev.sl4sh.polarity.commands.factions;

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

public class FactionsListMembers implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

       return CommandSpec.builder()
                .description(Text.of("Lists the members of a faction."))
                .arguments(GenericArguments.optional(new FactionCommandElement(Text.of("factionName"))))
                .permission("polarity.factions.list.members")
                .executor(new FactionsListMembers())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;
            String factionArg = "";

            if(args.getOne("factionName").isPresent()){

                factionArg = args.getOne("factionName").get().toString().toLowerCase();

            }

            if(factionArg.equals("")){

                if(Utilities.getPlayerFaction(ply).isPresent()){

                    Utilities.getPlayerFaction(ply).get().listMembers(ply);

                }
                else{

                    src.sendMessage(PolarityErrors.NOFACTION.getDesc());

                }

            }
            else{

                if(Utilities.getFactionByName(factionArg).isPresent()){

                    Utilities.getFactionByName(factionArg).get().listMembers(ply);

                }
                else{

                    src.sendMessage(PolarityErrors.NULLFACTION.getDesc());

                }

            }

        }
        else{

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

}
