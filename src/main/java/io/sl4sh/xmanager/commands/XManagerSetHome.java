package io.sl4sh.xmanager.commands;

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

public class XManagerSetHome implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            String homeName = "home";

            if(args.getOne("homeName").isPresent()){

                homeName = args.getOne("homeName").get().toString().toLowerCase();


            }

            boolean overWrite = args.getOne("overWrite").isPresent() && (Boolean)args.getOne("overWrite").get();

            setHome(homeName, ply, overWrite);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void setHome(String homeName, Player ply, boolean overWrite){

        XPlayer xPly = XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply);

        for(XHomeData homeLoc : xPly.getPlayerHomes()){

            if(homeLoc.getHomeName().equals(homeName)){

                if(!overWrite){

                    ply.sendMessage(Text.of(XError.XERROR_HNAMEEXISTS.getDesc()));
                    return;

                }

                xPly.getPlayerHomes().remove(homeLoc);
                break;

            }

        }

        xPly.addHome(new XHomeData(homeName, ply.getLocation().getBlockPosition().toString(), ply.getWorld().getDimension().getType().toString()));
        XManager.getXManager().writePlayerInfo();
        ply.sendMessage(Text.of("\u00a7aSuccessfully created a home called '" + homeName + "' in dimension " + ply.getWorld().getDimension().getType().toString() + "."));

    }

}
