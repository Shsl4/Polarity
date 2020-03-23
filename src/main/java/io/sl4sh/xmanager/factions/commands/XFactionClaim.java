package io.sl4sh.xmanager.factions.commands;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.XFactionContainer;
import io.sl4sh.xmanager.factions.XFactionPermissionData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            Optional<XFaction> optionalXFaction = XFactionCommandManager.getPlayerFaction(ply);

            if (optionalXFaction.isPresent()) {

                XFaction playerFaction = optionalXFaction.get();

                Optional<XFactionPermissionData> optPermData = XFactionCommandManager.getPlayerFactionPermissions(ply);

                if(!optPermData.isPresent()) { return; }

                if (optPermData.get().getClaim()) {

                    if (playerFaction.getFactionClaims() != null) {

                        if(isChunkAdjacentToClaimedChunks(chunkToClaim, ply)){

                            playerFaction.getFactionClaims().add(chunkToClaim.toString());

                            if (XManager.getXManager().writeFactions()) {

                                ply.sendMessage(Text.of("\u00a7a[Factions] | Chunk successfully claimed! " + chunkToClaim.toString()));

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

        Optional<XFaction> optPlayerFaction = XFactionCommandManager.getPlayerFaction(ply);

        if(!optPlayerFaction.isPresent()) { return false; }

        XFaction playerFaction = optPlayerFaction.get();

        if(playerFaction.getFactionClaims().size() == 0) {

            return true;

        }
        else {

            for(Vector3i adjCh : getAdjacentChunks(chunkLocation)){

                if(getClaimedChunkFaction(adjCh) != null && getClaimedChunkFaction(adjCh) == playerFaction){

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

