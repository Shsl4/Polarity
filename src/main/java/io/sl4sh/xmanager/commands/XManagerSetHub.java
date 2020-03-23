package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class XManagerSetHub implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player ply = (Player) src;
            setHub(ply);

        } else {

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void setHub(Player caller){

        Vector3d hudPos = caller.getPosition();
        XManager.getXManager().getConfigData().setHubLocation(hudPos.toString());
        XManager.getXManager().writeDataConfigFile();

        caller.sendMessage(Text.of("\u00a7a[XManager] | Successfully set the hub location!"));

    }

}
