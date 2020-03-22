package io.sl4sh.xmanager.factions.commands;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionContainer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class XFactionClaim implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            claimChunk(ply);

        } else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND));

        }

        return CommandResult.success();

    }

    void claimChunk(Player ply) {

        Vector3i chunkToClaim = ply.getLocation().getChunkPosition();

        if (!isChunkClaimed(chunkToClaim)) {

            if (XFactionCommandManager.getPlayerFaction(ply) != null) {

                if (XFactionCommandManager.getPlayerFactionPermissions(ply).getClaim()) {

                    if (XFactionCommandManager.getPlayerFaction(ply).getFactionClaims() != null) {

                        if(isChunkAdjacentToClaimedChunks(chunkToClaim, ply)){

                            XFactionCommandManager.getPlayerFaction(ply).getFactionClaims().add(chunkToClaim.toString());

                            if (XManager.getXManager().writeFactions()) {

                                ply.sendMessage(Text.of("\u00a7aChunk successfully claimed! " + chunkToClaim.toString()));

                            }

                        }

                        else{

                            ply.sendMessage(Text.of(XError.XERROR_NONADJCHUNK.getDesc()));

                        }

                    }

                } else {

                    ply.sendMessage(Text.of(XError.XERROR_NOTAUTHORIZED.getDesc()));

                }

            } else {

                ply.sendMessage(Text.of("\u00a7c" + XError.XERROR_NOXF.getDesc()));

            }

        } else {

            ply.sendMessage(Text.of(XError.XERROR_CHUNKCLAIMED.getDesc()));

        }

    }


    static public Boolean isChunkClaimed(Vector3i chunkLocation) {

        XFactionContainer factionContainer = XManager.getXManager().getFactionContainer();

        if (!factionContainer.getFactionList().isEmpty()) {

            for (XFaction faction : factionContainer.getFactionList()) {

                if (faction.getFactionClaims().contains(chunkLocation.toString())) {

                    return true;

                }

            }

        }


        return false;

    }

    boolean isChunkAdjacentToClaimedChunks(Vector3i chunkLocation, Player ply) {

        if (XFactionCommandManager.getPlayerFaction(ply).getFactionClaims().size() == 0) {

            return true;

        }
        else {

            for(Vector3i adjCh : getAdjacentChunks(chunkLocation)){

                if(getClaimedChunkFaction(adjCh) != null && getClaimedChunkFaction(adjCh) == XFactionCommandManager.getPlayerFaction(ply)){

                    return true;

                }

            }


        }

        return false;

    }

    static List<Vector3i> getAdjacentChunks(Vector3i chunkLocation) {

        List<Vector3i> adjChunks = new ArrayList<Vector3i>();

        adjChunks.add(chunkLocation.add(1, 0, 0));
        adjChunks.add(chunkLocation.add(1, 0, 1));
        adjChunks.add(chunkLocation.add(1, 0, -1));
        adjChunks.add(chunkLocation.add(-1, 0, 1));
        adjChunks.add(chunkLocation.add(-1, 0, 0));
        adjChunks.add(chunkLocation.add(-1, 0, -1));
        adjChunks.add(chunkLocation.add(0, 0, 1));
        adjChunks.add(chunkLocation.add(0, 0, -1));

        return adjChunks;

    }


    static public XFaction getClaimedChunkFaction(Vector3i chunkLocation) {

        XFactionContainer factionContainer = XManager.getXManager().getFactionContainer();

        for (XFaction faction : factionContainer.getFactionList()) {

            if (faction.getFactionClaims().contains(chunkLocation.toString())) {

                return faction;

            }

        }

        return null;

    }

}

