package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
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

            Player caller = (Player) src;

            if(args.getOne("factionName").isPresent()){

                if(!createFaction(caller, args.getOne("factionName").get().toString().toLowerCase())){

                    caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.75);

                }

            }
            else{

                caller.sendMessage(XError.XERROR_UNKNOWN.getDesc());

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean createFaction(Player caller, String newFactionName) {

        String factionName = XUtilities.getStringWithoutModifiers(newFactionName);

        if(!XUtilities.getPlayerFaction(caller).isPresent()){

            if(!XUtilities.doesFactionExistByName(factionName)){

                XFaction faction = new XFaction(factionName, caller);

                @Nonnull List<XFaction> factions = XManager.getFactions();

                factions.add(faction);
                caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully created your faction named " , factionName , "!"));
                caller.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, caller.getPosition(), 0.75);
                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();
                return true;

            }
            else{

                caller.sendMessage(Text.of(TextColors.RED, "[Factions] | A faction named " , factionName , " already exists!"));

            }

        }
        else{

            caller.sendMessage(XInfo.XERROR_XFMEMBER.getDesc());

        }

        return false;

    }

}
