package io.sl4sh.xmanager.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionContainer;
import io.sl4sh.xmanager.data.factions.XFactionPermissionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class XFactionsClaim implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Claim a chunk for your faction."))
                .permission("xmanager.factions.claim")
                .executor(new XFactionsClaim())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            claimChunk(ply);

        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    void claimChunk(Player ply) {

        Vector3i chunkToClaim = ply.getLocation().getChunkPosition();

        if(XUtilities.isLocationProtected(ply.getLocation())) { ply.sendMessage(XError.XERROR_PROTECTED.getDesc()); return; }

        if (!isChunkClaimed(chunkToClaim)) {

            Optional<XFaction> optionalXFaction = XUtilities.getPlayerFaction(ply);

            if (optionalXFaction.isPresent()) {

                XFaction playerFaction = optionalXFaction.get();

                Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                if(!optPermData.isPresent()) { return; }

                if (optPermData.get().getClaim()) {

                    if (playerFaction.getFactionClaims() != null) {

                        if(isChunkAdjacentToClaimedChunks(chunkToClaim, ply)){

                            playerFaction.getFactionClaims().add(chunkToClaim.toString());

                            if (XManager.getXManager().writeFactionsConfigurationFile()) {

                                ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Chunk successfully claimed! " , chunkToClaim.toString()));

                            }

                        }

                        else{

                            ply.sendMessage(XError.XERROR_NONADJCHUNK.getDesc());

                        }

                    }

                } else {

                    ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

                }

            } else {

                ply.sendMessage(XError.XERROR_NOXF.getDesc());

            }

        } else {

            ply.sendMessage(XInfo.XERROR_CHUNKCLAIMED.getDesc());

        }

    }


    static public Boolean isChunkClaimed(Vector3i chunkLocation) {

        XFactionContainer factionContainer = XManager.getXManager().getFactionsContainer();

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

        Optional<XFaction> optPlayerFaction = XUtilities.getPlayerFaction(ply);

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

        List<Vector3i> adjChunks = new ArrayList<>();
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

        XFactionContainer factionContainer = XManager.getXManager().getFactionsContainer();

        for (XFaction faction : factionContainer.getFactionList()) {

            if (faction.getFactionClaims().contains(chunkLocation.toString())) {

                return faction;

            }

        }

        return null;

    }

}
