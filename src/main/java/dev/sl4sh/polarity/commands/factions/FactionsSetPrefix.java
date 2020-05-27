package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityColor;
import dev.sl4sh.polarity.enums.PolarityErrors;
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

public class FactionsSetPrefix implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's prefix name."))
                .arguments(GenericArguments.string(Text.of("prefix")), GenericArguments.enumValue(Text.of("color"), PolarityColor.class))
                .permission("polarity.factions.setprefix")
                .executor(new FactionsSetPrefix())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            if(args.getOne("prefix").isPresent() && args.getOne("color").isPresent()){

                String prefix = args.getOne("prefix").get().toString();
                setFactionPrefix(ply, prefix, (PolarityColor)args.getOne("color").get());

            }
            else{

                setFactionPrefix(ply, "", PolarityColor.WHITE);

            }

        }
        else{

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void setFactionPrefix(Player caller, String factionPrefix, PolarityColor color){

        Optional<Faction> optXFac = Utilities.getPlayerFaction(caller);

        if(optXFac.isPresent()){

            Faction xFac = optXFac.get();

            Optional<FactionPermissionData> optPermData = Utilities.getPlayerFactionPermissions(caller);

            if(!optPermData.isPresent()) { caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

            if(optPermData.get().getManage()){

                if(factionPrefix.equals("")){

                    xFac.setPrefix("");
                    caller.sendMessage(Text.of(TextColors.GREEN, "Successfully removed your faction's prefix."));

                }
                else{

                    if(Utilities.getStringWithoutModifiers(factionPrefix).length() > 15){

                        caller.sendMessage(Text.of(PolarityErrors.LONGPREFIX.getDesc()));
                        return;

                    }

                    xFac.setPrefix(color.getStringColor() + ("[" + Utilities.getStringWithoutModifiers(factionPrefix) + "]"));
                    caller.sendMessage(Text.of(TextColors.GREEN, "Successfully updated your faction's prefix."));

                }

                Polarity.getPolarity().writeAllConfig();
                TabListManager.refreshAll();
                return;

            }
            else{

                caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc());

            }

        }

        caller.sendMessage(PolarityErrors.NOFACTION.getDesc());


    }

}
