package dev.sl4sh.polarity.commands.factions;

import com.flowpowered.math.vector.Vector3i;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.economy.PolarityEconomyService;
import dev.sl4sh.polarity.economy.currencies.PolarityCurrency;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
import dev.sl4sh.polarity.Utilities;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;
import java.util.Optional;

public class FactionsUnClaim implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Unclaims a chunk."))
                .permission("polarity.factions.unclaim")
                .executor(new FactionsUnClaim())
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

            src.sendMessage(PolarityErrors.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();
    }

    void unClaimChunk(Player ply){

        Optional<Faction> optPlyFac = Utilities.getPlayerFaction(ply);
        World world = ply.getWorld();
        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(world);

        if(optPlyFac.isPresent()){

            Faction playerFaction = optPlyFac.get();

            Optional<FactionPermissionData> optPermData = Utilities.getPlayerFactionPermissions(ply);

            if(!optPermData.isPresent()) { ply.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc()); return; }

            FactionPermissionData permData = optPermData.get();

            if(permData.getClaim()){

                Vector3i chunkPosition = ply.getLocation().getChunkPosition();

                if(worldInfo.isClaimed(chunkPosition)){

                    if(!Polarity.getEconomyService().isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

                    PolarityEconomyService economyService = Polarity.getEconomyService().get();

                    if(!economyService.getOrCreateAccount(playerFaction.getName()).isPresent()) { ply.sendMessage(Text.of(TextColors.RED, "[Economy] | Unable to access accounts. Please try again later.")); return; }

                    PolarityCurrency dollarCurrency = new PolarityCurrency();

                    economyService.getOrCreateAccount(playerFaction.getName()).get().deposit(dollarCurrency, BigDecimal.valueOf(75.0), Cause.of(EventContext.empty(), ply));

                    if(!worldInfo.removeClaim(chunkPosition, playerFaction.getUniqueId())) { ply.sendMessage(PolarityErrors.XERROR_UNKNOWN.getDesc()); return; }

                    ply.playSound(SoundTypes.BLOCK_NOTE_CHIME, ply.getPosition(), 0.75);

                    ply.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully unclaimed chunk! ", chunkPosition, " | Refunded ", dollarCurrency.format(BigDecimal.valueOf(75.0f), 2), TextColors.GREEN, "."));

                }
                else{

                    ply.sendMessage(PolarityInfo.XERROR_UNCLAIMEDCHUNK.getDesc());

                }

            }
            else{

                ply.sendMessage(PolarityErrors.XERROR_NOTAUTHORIZED.getDesc());

            }

        }
        else{

            ply.sendMessage(PolarityErrors.XERROR_NOXF.getDesc());

        }

    }

}
