package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionContainer;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.tablist.XTabListManager;
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

import java.util.ArrayList;
import java.util.List;

public class XFactionsCreate implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Creates a faction."))
                .permission("xmanager.factions.create")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("factionName"))))
                .executor(new XFactionsCreate())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("factionName").isPresent()){

                createFaction(ply, args.getOne("factionName").get().toString().toLowerCase());

            }
            else{

                ply.sendMessage(XError.XERROR_UNKNOWN.getDesc());

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void createFaction(Player creator, String newFactionName) {

        String factionName = XUtilities.getStringWithoutModifiers(newFactionName);

        if(!XUtilities.getPlayerFaction(creator).isPresent()){

            if(!XUtilities.doesFactionExist(factionName)){

                List<XFactionMemberData> factionMembers = new ArrayList<>();
                XFactionMemberData mbData = new XFactionMemberData(creator.getName(), new XFactionPermissionData(true, true, true));
                factionMembers.add(mbData);
                XFaction faction = new XFaction(factionName, "", "", creator.getName(), factionMembers, new ArrayList<>(), new XManagerLocationData(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

                XFactionContainer factions = XManager.getXManager().getFactionsContainer();

                if(factions != null){

                    factions.addFaction(faction);
                    XManager.getXManager().writeFactionsConfigurationFile();
                    creator.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully created your faction named " , factionName , "!"));
                    XTabListManager.refreshTabLists();


                }
                else{

                    creator.sendMessage(XError.XERROR_FILEREADFAIL.getDesc());

                }

            }
            else{

                creator.sendMessage(Text.of(TextColors.RED, "[Factions] | A faction named " , factionName , " already exists!"));

            }

        }
        else{

            creator.sendMessage(XInfo.XERROR_XFMEMBER.getDesc());

        }

    }



}
