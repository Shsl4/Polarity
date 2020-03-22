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

import java.util.ArrayList;
import java.util.List;

public class XManagerListHomes implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;

            listHomes(ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void listHomes(Player ply){

        XPlayer xPly = XManager.getXManager().getPlayerContainer().getXPlayerByPlayer(ply);

        ply.sendMessage(Text.of("\u00a72============ " + ply.getName() + " Homes ============"));

        if(xPly.getPlayerHomes().isEmpty()){

            ply.sendMessage(Text.of("\u00a7aNothing to see here... Yet!"));
            return;

        }

        int it = 0;

        List<String> dims = new ArrayList<>();

        for(XHomeData homeData : xPly.getPlayerHomes()){

            if(!dims.contains(homeData.getDimensionName())){

                dims.add(homeData.getDimensionName());

            }

        }

        for(String dim : dims){

            StringBuilder niceBuilder = new StringBuilder(dim.toLowerCase());
            niceBuilder.replace(0, 1, String.valueOf(niceBuilder.charAt(0)).toUpperCase());
            String niceDim = niceBuilder.toString().replace("_", " ");

            ply.sendMessage(Text.of("\u00a77### " + niceDim + ":"));

            for(XHomeData homeData : xPly.getPlayerHomes()){

                if(homeData.getDimensionName().equals(dim)){

                    ply.sendMessage(Text.of("\u00a7a#" + (it+1) + ". " + homeData.getHomeName() + " \u00a77| \u00a7d" + homeData.getHomeLocation()));
                    it++;

                }

            }

        }

    }

}
