package io.sl4sh.xmanager.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XFactionsUnClaim implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Unclaims a chunk."))
                .permission("xmanager.factions.unclaim")
                .executor(new XFactionsUnClaim())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            unClaimChunk(ply);

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();
    }

    void unClaimChunk(Player ply){

        Optional<XFaction> optPlyFac = XUtilities.getPlayerFaction(ply);

        if(optPlyFac.isPresent()){

            XFaction plyFac = optPlyFac.get();

            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            XFactionPermissionData permData = optPermData.get();

            if(permData.getClaim()){

                String worldName = ply.getWorld().getName();
                Vector3i chunkPosition = ply.getLocation().getChunkPosition();

                if(plyFac.isClaimed(worldName, chunkPosition)){

                    plyFac.removeClaim(worldName, chunkPosition);

                    ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully unclaimed chunk! ", chunkPosition));

                }
                else{

                    ply.sendMessage(XInfo.XERROR_UNCLAIMEDCHUNK.getDesc());

                }

            }
            else{

                ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            ply.sendMessage(XError.XERROR_NOXF.getDesc());

        }

    }

}
