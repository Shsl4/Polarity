package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import io.sl4sh.xmanager.enums.XPermissionTypes;
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

public class XFactionsSetPerm implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets a permission for a member of your faction."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.enumValue(Text.of("permName"), XPermissionTypes.class), GenericArguments.bool(Text.of("value")))
                .permission("xmanager.factions.setperm")
                .executor(new XFactionsSetPerm())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;

            if(args.getOne("targetPlayer").isPresent() && args.getOne("permName").isPresent() && args.getOne("value").isPresent()){

                Player targetPlayer = (Player)args.getOne("targetPlayer").get();
                XPermissionTypes targetPermission = (XPermissionTypes)args.getOne("permName").get();
                Boolean val = (Boolean)args.getOne("value").get();

                factionSetPerm(targetPlayer, targetPermission, val, ply);

            }
            else{

                ply.sendMessage(XError.XERROR_NULLPLAYER.getDesc());

            }

        }

        return CommandResult.success();

    }


    private void factionSetPerm(Player targetPlayer, XPermissionTypes targetPermission, Boolean value, Player caller){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(optCallerFaction.isPresent()){

            XFaction callerFaction = optCallerFaction.get();

            Optional<XFactionPermissionData> optCallerPermData = XUtilities.getPlayerFactionPermissions(caller);

            if(!optCallerPermData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            XFactionPermissionData callerPermData = optCallerPermData.get();

            if(callerPermData.getManage()){

                Optional<XFactionPermissionData> optTargetPermData = XUtilities.getPlayerFactionPermissions(targetPlayer);

                if(!optTargetPermData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return;}

                XFactionPermissionData targetPermData = optTargetPermData.get();

                if(XUtilities.getPlayerFaction(targetPlayer).isPresent() && XUtilities.getPlayerFaction(targetPlayer).get() == callerFaction){

                    if(!callerFaction.isOwner(targetPlayer.getName())){

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

                    caller.sendMessage(XError.XERROR_NOTAMEMBER.getDesc());

                }

            }
            else{

                caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            caller.sendMessage(XError.XERROR_NOXF.getDesc());

        }

    }

}
