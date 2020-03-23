package io.sl4sh.xmanager.factions.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionMemberData;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionUnClaim implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            unClaimChunk(ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    void unClaimChunk(Player ply){

        Optional<XFaction> optPlyFac = XFactionCommandManager.getPlayerFaction(ply);

        if(optPlyFac.isPresent()){

            XFaction plyFac = optPlyFac.get();

            Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc())); return; }

            XFactionPermissionData permData = optPermData.get();

            if(permData.getClaim()){

                String targetChunk = ply.getLocation().getChunkPosition().toString();

                if(plyFac.getFactionClaims().contains(targetChunk)){

                    plyFac.getFactionClaims().remove(targetChunk);

                    ply.sendMessage(Text.of("\u00a7a[Factions] | Successfully unclaimed chunk! " + targetChunk));

                }
                else{

                    ply.sendMessage(Text.of(XError.XERROR_UNCLAIMEDCHUNK.getDesc()));

                }

            }
            else{

                ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

            }

        }
        else{

            ply.sendMessage(Text.of(XError.XERROR_NOXF.getDesc()));

        }

    }

}
