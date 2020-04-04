package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.data.XWorldInfo;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
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

public class XFactionsHome implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Teleport to your faction's home."))
                .permission("xmanager.factions.home")
                .executor(new XFactionsHome())
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

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();
    }

    private void factionHome(Player caller){

        Optional<XFaction> OptCallerFaction = XUtilities.getPlayerFaction(caller);

        if(!OptCallerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = OptCallerFaction.get();

        XWorldInfo worldInfo = XUtilities.getOrCreateWorldInfo(caller.getWorld());

        if(!XUtilities.getFactionHomeWorld(callerFaction.getUniqueId()).isPresent()) { caller.sendMessage(XError.XERROR_NOHOME.getDesc()); return;  }

        Optional<World> optHomeWorld = XUtilities.getFactionHomeWorld(callerFaction.getUniqueId());

        if(!optHomeWorld.isPresent()) {  caller.sendMessage(Text.of(TextColors.RED, "This home can't be accessed right now.")); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<>(optHomeWorld.get(), XUtilities.getOrCreateWorldInfo(optHomeWorld.get()).getFactionHome(callerFaction.getUniqueId()).get()));

        if(safeLoc.isPresent()){

            caller.setLocation(safeLoc.get());
            caller.playSound(SoundTypes.ENTITY_ENDERMEN_TELEPORT, caller.getPosition(), 0.75);

        }
        else{

            caller.sendMessage(XError.XERROR_NOSAFELOC.getDesc());

        }

    }

}
