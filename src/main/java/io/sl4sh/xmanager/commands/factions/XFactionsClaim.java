package io.sl4sh.xmanager.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XWorldInfo;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
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
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        if(XUtilities.isLocationProtected(ply.getLocation())) { ply.sendMessage(XError.XERROR_PROTECTED.getDesc()); return; }

        Vector3i chunkPosition = ply.getLocation().getChunkPosition();
        World world = ply.getWorld();
        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(world);

        if (!worldInfo.getClaimedChunkFaction(chunkPosition).isPresent()) {

            Optional<XFaction> optionalXFaction = XUtilities.getPlayerFaction(ply);

            if (!optionalXFaction.isPresent()) { ply.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

            XFaction playerFaction = optionalXFaction.get();

            Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { return; }

            if (optPermData.get().getClaim()) { ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

            if(isChunkAdjacentToClaimedChunks(world, chunkPosition)) {  ply.sendMessage(XError.XERROR_NONADJCHUNK.getDesc()); return; }

            if(!XManager.getEconomyService().isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

            XEconomyService economyService = XManager.getEconomyService().get();

            if(!economyService.getOrCreateAccount(playerFaction.getName()).isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

            XDollar dollarCurrency = new XDollar();

            TransactionResult result = economyService.getOrCreateAccount(playerFaction.getName()).get().withdraw(dollarCurrency, BigDecimal.valueOf(125.0), Cause.of(EventContext.empty(), ply));

            switch (result.getResult()){

                case SUCCESS:

                    worldInfo.addClaim(chunkPosition, playerFaction.getUniqueId());
                    ply.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, ply.getPosition(), 0.75);
                    ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Chunk successfully claimed! " , ply.getLocation().getChunkPosition().toString(), " | Paid ", dollarCurrency.format(BigDecimal.valueOf(125.0f), 2), TextColors.GREEN, "."));
                    XManager.getXManager().writeFactionsConfigurationFile();
                    return;

                    case ACCOUNT_NO_FUNDS:

                        ply.sendMessage(Text.of(TextColors.RED, "[Economy] | You faction does not have enough money to do that!"));
                        return;

            }

            ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));


        } else {

            Optional<XFaction> optFaction = XUtilities.getFactionByUniqueID(worldInfo.getClaimedChunkFaction(chunkPosition).get());

            if(optFaction.isPresent()){

                ply.sendMessage(Text.of(TextColors.AQUA, "[Factions] | This chunk is already claimed. Owned by ", optFaction.get().getDisplayName(), TextColors.AQUA, "."));

            }
            else{

                ply.sendMessage(Text.of(TextColors.AQUA, "[Factions] | This chunk is already claimed."));

            }

        }

    }

    boolean isChunkAdjacentToClaimedChunks(World world, Vector3i location) {

        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(world);
        Optional<UUID> optionalUUID = worldInfo.getClaimedChunkFaction(location);

        if(optionalUUID.isPresent()){

            UUID factionID = optionalUUID.get();

            if(worldInfo.getFactionClaimedChunks(optionalUUID.get()).size() == 0) {

                return true;

            }
            else {

                for(Vector3i adjCh : getAdjacentChunks(location)){

                    Optional<UUID> optTargetID = worldInfo.getClaimedChunkFaction(adjCh);

                    if(optTargetID.isPresent() && optTargetID.get().equals(factionID)){

                        return true;

                    }

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

}

