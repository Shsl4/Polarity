package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColors;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import dev.sl4sh.polarity.TabListManager;
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

public class FactionsSetDisplayName implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's display name."))
                .arguments(GenericArguments.string(Text.of("displayName")), GenericArguments.enumValue(Text.of("color"), PolarityColors.class))
                .permission("polarity.factions.setdisplayname")
                .executor(new FactionsSetDisplayName())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("displayName").isPresent() && args.getOne("color").isPresent()){

                String displayName = args.getOne("displayName").get().toString();
                setFactionDisplayName(displayName, ply, (PolarityColors)args.getOne("color").get());

            }

        }
        else{

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void setFactionDisplayName(String displayName, Player ply, PolarityColors color){

        Optional<Faction> optXFac = Utilities.getPlayerFaction(ply);

        if(optXFac.isPresent()){

            Optional<FactionPermissionData> optPermData = Utilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

            if(optPermData.get().getManage()){

                if(displayName.equals("")){

                    optXFac.get().setDisplayName(optXFac.get().getName());
                    ply.sendMessage(Text.of(TextColors.GREEN, "Successfully removed your faction's display name."));

                }
                else{

                    optXFac.get().setDisplayName(color.getStringColor() + Utilities.getStringWithoutModifiers(displayName));
                    ply.sendMessage(Text.of(TextColors.GREEN, "Successfully updated your faction's display name."));

                }

                Polarity.getPolarity().writeAllConfig();
                TabListManager.refreshTabLists();
                return;

            }
            else{

                ply.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc());

            }

        }

        ply.sendMessage(PolarityErrors.NOFACTION.getDesc());

    }

}
