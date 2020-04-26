package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.Utilities;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
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

public class FactionsSetHome implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's home."))
                .permission("polarity.factions.sethome")
                .executor(new FactionsSetHome())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            setFactionHome(ply);

        }
        else{

            src.sendMessage(Text.of(PolarityErrors.PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void setFactionHome(Player caller){

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(PolarityErrors.NOFACTION.getDesc()); return; }

        Faction callerFaction = optCallerFaction.get();

        if(!Utilities.getPlayerFactionPermissions(caller).isPresent() || !Utilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(PolarityErrors.UNAUTHORIZED.getDesc()); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<>(caller.getWorld(), caller.getPosition()));

        if(safeLoc.isPresent()){

            WorldInfo worldInfo = Utilities.getOrCreateWorldInfo(caller.getWorld());
            worldInfo.setFactionHome(callerFaction.getUniqueId(), safeLoc.get().getPosition());
            Polarity.getPolarity().writeAllConfig();
            caller.sendMessage(Text.of(TextColors.GREEN, "Successfully set your faction's home!"));

        }
        else{

            caller.sendMessage(PolarityErrors.NOSAFELOC.getDesc());

        }

    }

}
