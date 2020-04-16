package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
import dev.sl4sh.polarity.enums.PolarityFactionPermissionTypes;
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

public class FactionsSetPerm implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets a permission for a member of your faction."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.enumValue(Text.of("permName"), PolarityFactionPermissionTypes.class), GenericArguments.bool(Text.of("value")))
                .permission("polarity.factions.setperm")
                .executor(new FactionsSetPerm())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;

            if(args.getOne("targetPlayer").isPresent() && args.getOne("permName").isPresent() && args.getOne("value").isPresent()){

                Player targetPlayer = (Player)args.getOne("targetPlayer").get();
                PolarityFactionPermissionTypes targetPermission = (PolarityFactionPermissionTypes)args.getOne("permName").get();
                Boolean val = (Boolean)args.getOne("value").get();

                factionSetPerm(targetPlayer, targetPermission, val, ply);

            }
            else{

                ply.sendMessage(PolarityErrors.XERROR_NULLPLAYER.getDesc());

            }

        }

        return CommandResult.success();

    }


    private void factionSetPerm(Player targetPlayer, PolarityFactionPermissionTypes targetPermission, Boolean value, Player caller){

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        if(optCallerFaction.isPresent()){

            Faction callerFaction = optCallerFaction.get();

            Optional<FactionPermissionData> optCallerPermData = Utilities.getPlayerFactionPermissions(caller);

            if(!optCallerPermData.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

            FactionPermissionData callerPermData = optCallerPermData.get();

            if(callerPermData.getManage()){

                Optional<FactionPermissionData> optTargetPermData = Utilities.getPlayerFactionPermissions(targetPlayer);

                if(!optTargetPermData.isPresent()) { caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return;}

                FactionPermissionData targetPermData = optTargetPermData.get();

                if(Utilities.getPlayerFaction(targetPlayer).isPresent() && Utilities.getPlayerFaction(targetPlayer).get() == callerFaction){

                    if(!callerFaction.isOwner(targetPlayer)){

                        switch (targetPermission){

                            case Interact:

                                targetPermData.setInteract(value);
                                break;

                            case Claim:

                                targetPermData.setClaim(value);
                                break;

                            case Manage:

                                targetPermData.setManage(value);
                                break;

                        }

                        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully set " , targetPermission , " permission to " , value , " for player " , targetPlayer.getName()));
                    }
                    else{

                        caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | The owner's permissions can't be changed"));

                    }

                }
                else{

                    caller.sendMessage(PolarityErrors.XERROR_NOTAMEMBER.getDesc());

                }

            }
            else{

                caller.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            caller.sendMessage(PolarityErrors.XERROR_NOXF.getDesc());

        }

    }

}
