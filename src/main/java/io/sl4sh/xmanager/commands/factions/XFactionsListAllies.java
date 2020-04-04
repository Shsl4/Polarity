package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.commands.elements.XFactionCommandElement;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
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

public class XFactionsListAllies implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Lists the allies of a faction."))
                .arguments(GenericArguments.optional(new XFactionCommandElement(Text.of("factionName"))))
                .permission("xmanager.factions.list.allies")
                .executor(new XFactionsListAllies())
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

                Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

                if(!optCallerFaction.isPresent()) {  caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

                optCallerFaction.get().listAllies(caller);

            }
            else{

                src.sendMessage(Text.of(TextColors.AQUA, "[Factions] | As you are not a player, you need to specify a faction name"));

            }

        }
        else{

            Optional<XFaction> optTargetFaction = XUtilities.getFactionByName(targetFactionName);

            if(!optTargetFaction.isPresent()) { src.sendMessage(XError.XERROR_XFNULL.getDesc()); return; }

            optTargetFaction.get().listAllies(src);

        }

    }

}
