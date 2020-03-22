package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XHomeData;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.player.XPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Vector;

public class XManagerHome implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            String homeName = "home";

            if(args.getOne("homeName").isPresent()){

                homeName = args.getOne("homeName").get().toString().toLowerCase();

            }

            getHome(homeName, ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void getHome(String homeName, Player ply){

        XPlayer xPly = XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply);

        for(XHomeData homeData : xPly.getPlayerHomes()){

            if(homeData.getHomeName().equals(homeName)){

                if(homeData.getDimensionName().equals(ply.getWorld().getDimension().getType().toString())){

                    Optional<Location<World>> safeLoc = Sponge.getGame().getTeleportHelper().getSafeLocation(new Location<World>(ply.getWorld(), XManager.getStringAsVector3i(homeData.getHomeLocation())));

                    if(safeLoc.isPresent()){

                        ply.setLocation(safeLoc.get());
                        ply.sendMessage(Text.of("\u00a7aSuccessfully teleported at home '" + homeName + "'."));

                    }
                    else{

                        ply.sendMessage(Text.of(XError.XERROR_NOSAFELOC.getDesc()));

                    }

                    return;

                }
                else{

                    ply.sendMessage(Text.of(XError.XERROR_WRONGDIM.getDesc()));
                    return;

                }

            }

        }

        ply.sendMessage(Text.of(XError.XERROR_NOHOME.getDesc()));

    }

}
