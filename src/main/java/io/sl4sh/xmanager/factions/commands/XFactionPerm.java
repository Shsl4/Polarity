package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.swing.text.html.Option;
import java.util.Optional;

public class  XFactionPerm implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;

            if(args.getOne("targetPlayer").isPresent()){

                Player targetPlayer = (Player)args.getOne("targetPlayer").get();
                String permName = args.getOne("permName").get().toString();
                Boolean val = (Boolean)args.getOne("value").get();

                factionSetPerm(targetPlayer, permName, val, ply);

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc()));

            }

        }

        return CommandResult.success();

    }


    private void factionSetPerm(Player targetPlayer, String permName, Boolean value, Player caller){

        Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(caller);

        if(optCallerFaction.isPresent()){

            XFaction callerFaction = optCallerFaction.get();

            Optional<XFactionPermissionData> optCallerPermData = XFactionCommandManager.getPlayerFactionPermissions(caller);

            if(!optCallerPermData.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

            XFactionPermissionData callerPermData = optCallerPermData.get();

            if(callerPermData.getConfigure()){

                Optional<XFactionPermissionData> optTargetPermData = XFactionCommandManager.getPlayerFactionPermissions(targetPlayer);

                if(!optTargetPermData.isPresent()) { caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return;}

                XFactionPermissionData targetPermData = optTargetPermData.get();

                if(XFactionCommandManager.getPlayerFaction(targetPlayer).isPresent() && XFactionCommandManager.getPlayerFaction(targetPlayer).get() == callerFaction){

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

                                caller.sendMessage(Text.of(XError.XERROR_INVALIDPERM.getDesc()));
                                return;

                        }

                        caller.sendMessage(Text.of("\u00a7aSuccessfully set " + permName + " permission to " + value + " for player " + targetPlayer.getName()));

                    }
                    else{

                        caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

                    }

                }
                else{

                    caller.sendMessage(Text.of(XError.XERROR_NOTAMEMBER.getDesc()));

                }

            }
            else{

                caller.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

            }

        }
        else{

            caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));

        }

    }

}
