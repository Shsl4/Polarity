package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XHomeData;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.player.XPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XManagerRemoveHome implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            if(args.getOne("homeName").isPresent()){

                String homeName = args.getOne("homeName").get().toString().toLowerCase();
                removeHome(homeName, ply);

            }

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void removeHome(String homeName, Player ply){

        XPlayer xPly = XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply);

        for(XHomeData homeData : xPly.getPlayerHomes()){

            if(homeData.getHomeName().equals(homeName)){

                xPly.getPlayerHomes().remove(homeData);
                ply.sendMessage(Text.of("\u00a7aSuccessfully removed your home named '" + homeName + "'."));
                return;

            }

        }

        ply.sendMessage(Text.of(XError.XERROR_NOHOME.getDesc()));

    }

}
