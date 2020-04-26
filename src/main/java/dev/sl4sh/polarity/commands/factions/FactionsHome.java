package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Faction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class FactionsHome implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleport to your faction's home."))
                .permission("polarity.factions.home")
                .executor(new FactionsHome())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            factionHome(ply);

        }
        else{

            src.sendMessage(PolarityErrors.PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();
    }

    private void factionHome(Player caller){

        Optional<Faction> OptCallerFaction = Utilities.getPlayerFaction(caller);

        if(!OptCallerFaction.isPresent()) { caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

        Faction callerFaction = OptCallerFaction.get();

        WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());

        if(!Utilities.getFactionHomeWorld(callerFaction.getUniqueId()).isPresent()) { caller.sendMessage(PolarityErrors.NOHOME.getDesc()); return;  }

        Optional<World> optHomeWorld = Utilities.getFactionHomeWorld(callerFaction.getUniqueId());

        if(!optHomeWorld.isPresent()) {  caller.sendMessage(Text.of(TextColors.RED, "This home can't be accessed right now.")); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<>(optHomeWorld.get(), Utilities.getOrCreateWorldInfo(optHomeWorld.get()).getFactionHome(callerFaction.getUniqueId()).get()));

        if(safeLoc.isPresent()){

            caller.setLocation(safeLoc.get());
            caller.playSound(SoundTypes.ENTITY_ENDERMEN_TELEPORT, caller.getPosition(), 0.25);

        }
        else{

            caller.sendMessage(PolarityErrors.NOSAFELOC.getDesc());

        }

    }

}
