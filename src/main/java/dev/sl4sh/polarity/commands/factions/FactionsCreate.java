package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
import dev.sl4sh.polarity.tablist.TabListManager;
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

public class FactionsCreate implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Creates a faction."))
                .permission("polarity.factions.create")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("factionName"))))
                .executor(new FactionsCreate())
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

                caller.sendMessage(PolarityErrors.XERROR_UNKNOWN.getDesc());

            }

        }
        else{

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean createFaction(Player caller, String newFactionName) {

        String factionName = Utilities.getStringWithoutModifiers(newFactionName);

        if(!Utilities.getPlayerFaction(caller).isPresent()){

            if(!Utilities.doesFactionExistByName(factionName)){

                Faction faction = new Faction(factionName, caller);

                @Nonnull List<Faction> factions = Polarity.getFactions().getList();

                factions.add(faction);
                caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully created your faction named " , factionName , "!"));
                caller.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, caller.getPosition(), 0.75);
                Polarity.getPolarity().writeAllConfig();
                TabListManager.refreshTabLists();
                return true;

            }
            else{

                caller.sendMessage(Text.of(TextColors.RED, "[Factions] | A faction named " , factionName , " already exists!"));

            }

        }
        else{

            caller.sendMessage(PolarityInfo.XERROR_XFMEMBER.getDesc());

        }

        return false;

    }

}
