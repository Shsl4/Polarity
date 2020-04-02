package io.sl4sh.xmanager.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.currencies.XDollar;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
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

        if(XUtilities.isLocationProtected(ply.getLocation())) { ply.sendMessage(XError.XERROR_PROTECTED.getDesc()); return; }

        String worldName = ply.getWorld().getName();
        Vector3i chunkPosition = ply.getLocation().getChunkPosition();

        if (!getClaimedChunkFaction(worldName, chunkPosition).isPresent()) {

            Optional<XFaction> optionalXFaction = XUtilities.getPlayerFaction(ply);

            if (optionalXFaction.isPresent()) {

                XFaction playerFaction = optionalXFaction.get();

                Optional<XFactionPermissionData> optPermData = XUtilities.getPlayerFactionPermissions(ply);

                if(!optPermData.isPresent()) { return; }

                if (optPermData.get().getClaim()) {

                    if(isChunkAdjacentToClaimedChunks(worldName, chunkPosition, ply)){

                        if(!XManager.getXEconomyService().isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

                        XEconomyService economyService = XManager.getXEconomyService().get();

                        if(!economyService.getOrCreateAccount(playerFaction.getFactionName()).isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

                        XDollar dollarCurrency = new XDollar();

                        TransactionResult result = economyService.getOrCreateAccount(playerFaction.getFactionName()).get().withdraw(dollarCurrency, BigDecimal.valueOf(125.0), Cause.of(EventContext.empty(), ply));

                        switch (result.getResult()){

                            case SUCCESS:

                                playerFaction.getFactionClaims().add(new XManagerLocationData(ply.getWorld().getName(), ply.getLocation().getChunkPosition().toString()));
                                ply.playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, ply.getPosition(), 0.75);
                                ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Chunk successfully claimed! " , ply.getLocation().getChunkPosition().toString(), " | Paid ", dollarCurrency.format(BigDecimal.valueOf(125.0f), 2), TextColors.GREEN, "."));
                                XManager.getXManager().writeFactionsConfigurationFile();
                                return;

                            case ACCOUNT_NO_FUNDS:

                                ply.sendMessage(Text.of(TextColors.RED, "[Economy] | You faction does not have enough money to do that!"));
                                return;

                        }

                        ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Transaction Failed!"));

                    }

                    else{

                        ply.sendMessage(XError.XERROR_NONADJCHUNK.getDesc());

                    }

                } else {

                    ply.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc());

                }

            } else {

                ply.sendMessage(XError.XERROR_NOXF.getDesc());

            }

        } else {

            XFaction optFaction = getClaimedChunkFaction(worldName, chunkPosition).get();

            ply.sendMessage(Text.of(TextColors.AQUA, "[Factions] | This chunk is already claimed. Owned by ", optFaction.getFactionDisplayName(), TextColors.AQUA, "."));

        }

    }

    static public Optional<XFaction> getClaimedChunkFaction(String worldName, Vector3i location) {

        List<XFaction> factionsContainer = XManager.getFactions();

        for (XFaction faction : factionsContainer) {

            if(faction.isClaimed(worldName, location)){

                return Optional.of(faction);

            }

        }

        return Optional.empty();

    }

    boolean isChunkAdjacentToClaimedChunks(String worldName, Vector3i location, Player ply) {

        Optional<XFaction> optPlayerFaction = XUtilities.getPlayerFaction(ply);

        if(!optPlayerFaction.isPresent()) { return false; }

        XFaction playerFaction = optPlayerFaction.get();

        if(playerFaction.getFactionClaims().size() == 0) {

            return true;

        }
        else {

            for(Vector3i adjCh : getAdjacentChunks(location)){

                Optional<XFaction> optOwningFaction = getClaimedChunkFaction(worldName, adjCh);

                if(optOwningFaction.isPresent() && optOwningFaction.get() == playerFaction){

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





}

