package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionMemberRank;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class  XFactionPerm implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player)src;

            String targetPlayer = args.getOne("targetPlayer").get().toString();
            String permName = args.getOne("permName").get().toString();
            Boolean val = (Boolean)args.getOne("value").get();

            factionSetPerm(targetPlayer, permName, val, ply);


        }

        return CommandResult.success();

    }


    private void factionSetPerm(String targetPlayer, String permName, Boolean value, Player caller){

        XFaction fac = XFactionCommandManager.getPlayerFaction(caller);

        if(fac != null){

            if(XFactionCommandManager.getPlayerFactionPermissions(caller).getConfigure()){

                if(Sponge.getServer().getPlayer(targetPlayer).isPresent()){

                    Player nPlayer = Sponge.getServer().getPlayer(targetPlayer).get();
                    XFactionPermissionData permData = XFactionCommandManager.getPlayerFactionPermissions(nPlayer);

                    if(XFactionCommandManager.getPlayerFaction(nPlayer) == fac){

                        if(permData.getRank() != XFactionMemberRank.Owner){

                            switch (permName){

                                case "interact":

                                    permData.setInteract(value);
                                    break;

                                case "claim":

                                    permData.setClaim(value);
                                    break;

                                case "configure":

                                    permData.setConfigure(value);

                                    break;

                                default:

                                    caller.sendMessage(Text.of(XError.XERROR_INVALIDPERM.getDesc()));
                                    return;

                            }

                            caller.sendMessage(Text.of("\u00a7aSuccessfully set " + permName + " permission to " + value + " for player " + targetPlayer));

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

                    caller.sendMessage(Text.of(XError.XERROR_NULLPLAYER.getDesc()));

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
