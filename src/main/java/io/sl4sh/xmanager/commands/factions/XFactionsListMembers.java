package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.commands.elements.XFactionCommandElement;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
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

public class XFactionsListMembers implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

       return CommandSpec.builder()
                .description(Text.of("Lists the members of a faction."))
                .arguments(GenericArguments.optional(new XFactionCommandElement(Text.of("factionName"))))
                .permission("xmanager.factions.list.members")
                .executor(new XFactionsListMembers())
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

                if(XUtilities.getPlayerFaction(ply).isPresent()){

                    XUtilities.getPlayerFaction(ply).get().listMembers(ply);

                }
                else{

                    src.sendMessage(XError.XERROR_NOXF.getDesc());

                }

            }
            else{

                if(XUtilities.getFactionByName(factionArg).isPresent()){

                    XUtilities.getFactionByName(factionArg).get().listMembers(ply);

                }
                else{

                    src.sendMessage(XError.XERROR_XFNULL.getDesc());

                }

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

}
