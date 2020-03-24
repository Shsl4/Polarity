package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
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

public class XFactionsPerm implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets a permission for a member of your faction."))
                .arguments(GenericArguments.player(Text.of("targetPlayer")), GenericArguments.string(Text.of("permName")), GenericArguments.bool(Text.of("value")))
                .permission("xmanager.factions.perm")
                .executor(new XFactionsPerm())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;

            if(args.getOne("targetPlayer").isPresent() && args.getOne("permName").isPresent() && args.getOne("value").isPresent()){

                Player targetPlayer = (Player)args.getOne("targetPlayer").get();
                String permName = args.getOne("permName").get().toString();
                Boolean val = (Boolean)args.getOne("value").get();

                factionSetPerm(targetPlayer, permName, val, ply);

            }
            else{

                ply.sendMessage(XError.XERROR_NULLPLAYER.getDesc());

            }

        }

        return CommandResult.success();

    }


    private void factionSetPerm(Player targetPlayer, String permName, Boolean value, Player caller){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(optCallerFaction.isPresent()){

            XFaction callerFaction = optCallerFaction.get();

            Optional<XFactionPermissionData> optCallerPermData = XUtilities.getPlayerFactionPermissions(caller);

            if(!optCallerPermData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            XFactionPermissionData callerPermData = optCallerPermData.get();

            if(callerPermData.getConfigure()){

                Optional<XFactionPermissionData> optTargetPermData = XUtilities.getPlayerFactionPermissions(targetPlayer);

                if(!optTargetPermData.isPresent()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return;}

                XFactionPermissionData targetPermData = optTargetPermData.get();

                if(XUtilities.getPlayerFaction(targetPlayer).isPresent() && XUtilities.getPlayerFaction(targetPlayer).get() == callerFaction){

                    if(!callerFaction.isOwner(targetPlayer.getName())){

                        switch (permName){

                            case "interact":

                                targetPermData.setInteract(value);
                                break;

                            case "claim":

                                targetPermData.setClaim(value);
                                break;

                            case "configure":

                                targetPermData.setConfigure(value);
                                break;

                            default:

                                caller.sendMessage(XError.XERROR_INVALIDPERM.getDesc());
                                return;

                        }

                        caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully set " , permName , " permission to " , value , " for player " , targetPlayer.getName()));
                    }
                    else{

                        caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

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
