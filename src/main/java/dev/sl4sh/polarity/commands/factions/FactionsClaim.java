package dev.sl4sh.polarity.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.data.factions.FactionPermissionData;
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

public class FactionsClaim implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Claim a chunk for your faction."))
                .permission("polarity.factions.claim")
                .executor(new FactionsClaim())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            claimChunk(ply);

        } else {

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    void claimChunk(Player ply) {

        if(Utilities.isLocationProtected(ply.getLocation()) || Utilities.getOrCreateWorldInfo(ply.getWorld()).isGameWorld()) { ply.sendMessage(PolarityErrors.PROTECTED.getDesc()); return; }

        Vector3i chunkPosition = ply.getLocation().getChunkPosition();
        World world = ply.getWorld();
        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(world);

        if (!worldInfo.getClaimedChunkFaction(chunkPosition).isPresent()) {

            Optional<Faction> optionalFaction = Utilities.getPlayerFaction(ply);

            if (!optionalFaction.isPresent()) { ply.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

            Faction playerFaction = optionalFaction.get();

            Optional<FactionPermissionData> optPermData = Utilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { return; }

            if (!optPermData.get().getClaim()) { ply.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

            if(!isChunkAdjacentToClaimedChunks(worldInfo, playerFaction.getUniqueId(), chunkPosition)) { ply.sendMessage(PolarityErrors.NONADJCHUNK.getDesc()); return; }

            if(!Polarity.getEconomyService().isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return; }

            PolarityEconomyService economyService = Polarity.getEconomyService().get();

            if(!economyService.getOrCreateAccount(playerFaction.getUniqueId()).isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "Unable to access accounts. Please try again later.")); return; }

            PolarityCurrency dollarCurrency = new PolarityCurrency();

            int numClaimedChunks = Utilities.getAllFactionClaims(playerFaction.getUniqueId()).size() + 1;

            TransactionResult result = economyService.getOrCreateAccount(playerFaction.getUniqueId()).get().withdraw(dollarCurrency, BigDecimal.valueOf(2500 * (numClaimedChunks / 10.0)), Cause.of(EventContext.empty(), ply));

            switch (result.getResult()){

                case SUCCESS:

                    worldInfo.addClaim(chunkPosition, playerFaction.getUniqueId());
                    ply.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, ply.getPosition(), .25);
                    ply.sendMessage(Text.of(TextColors.GREEN, "Chunk successfully claimed! " , ply.getLocation().getChunkPosition().toString(), " | Paid ", dollarCurrency.format(BigDecimal.valueOf(2500 * (numClaimedChunks / 10.0)), 2), TextColors.GREEN, "."));
                    Polarity.getPolarity().writeAllConfig();
                    return;

                    case ACCOUNT_NO_FUNDS:

                        ply.sendMessage(Text.of(TextColors.RED, "You faction does not have enough money to do that!"));
                        return;

            }

            ply.sendMessage(Text.of(TextColors.RED, "Transaction Failed!"));


        } else {

            Optional<Faction> optFaction = Utilities.getFactionByUniqueID(worldInfo.getClaimedChunkFaction(chunkPosition).get());

            if(optFaction.isPresent()){

                ply.sendMessage(Text.of(TextColors.AQUA, "This chunk is already claimed. Owned by ", optFaction.get().getDisplayName(), TextColors.AQUA, "."));

            }
            else{

                ply.sendMessage(Text.of(TextColors.AQUA, "This chunk is already claimed."));

            }

        }

    }

    boolean isChunkAdjacentToClaimedChunks(WorldInfo worldInfo, UUID callerFactionID, Vector3i location) {

        if(worldInfo.getFactionClaimedChunks(callerFactionID).size() <= 0){

            return true;

        }

        for(Vector3i adjCh : getAdjacentChunks(location)){

            Optional<UUID> optTargetID = worldInfo.getClaimedChunkFaction(adjCh);

            if(optTargetID.isPresent() && optTargetID.get().equals(callerFactionID)){

                return true;

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

