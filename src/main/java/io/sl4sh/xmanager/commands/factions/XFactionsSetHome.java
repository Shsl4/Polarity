package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XManagerLocationData;
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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XFactionsSetHome implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets your faction's home."))
                .permission("xmanager.factions.sethome")
                .executor(new XFactionsSetHome())
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

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();
    }

    private void setFactionHome(Player caller){

        Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(caller);

        if(!optCallerFaction.isPresent()) { caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(!XUtilities.getPlayerFactionPermissions(caller).isPresent() || !XUtilities.getPlayerFactionPermissions(caller).get().getManage()) { caller.sendMessage(XError.XERROR_NOTAUTHORIZED.getDesc()); return; }

        Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<>(caller.getWorld(), caller.getPosition()));

        if(safeLoc.isPresent()){

            callerFaction.setFactionHome(new XManagerLocationData(caller.getWorld().getName(), caller.getPosition().toString()));
            XManager.getXManager().writeFactionsConfigurationFile();
            caller.sendMessage(Text.of(TextColors.GREEN, "[Factions] | Successfully set your faction's home!"));

        }
        else{

            caller.sendMessage(XError.XERROR_NOSAFELOC.getDesc());

        }

    }

}
