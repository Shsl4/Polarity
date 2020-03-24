package io.sl4sh.xmanager.commands;

import com.flowpowered.math.vector.Vector3d;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class XManagerSetHub implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets the hub location."))
                .permission("xmanager.sethub")
                .executor(new XManagerSetHub())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        // Allow the command execution only if the caller is a player.
        if (src instanceof Player) {

            Player ply = (Player) src;
            setHub(ply);

        } else {

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void setHub(Player caller){

        // Just set and save the information (No error should occur)
        Vector3d hubPos = caller.getPosition();
        XManager.getXManager().getConfigData().getHubData().setLocation(hubPos.toString());
        XManager.getXManager().getConfigData().getHubData().setDimensionName(caller.getWorld().getName());
        XManager.getXManager().writeMainDataConfigurationFile();

        caller.sendMessage(Text.of(TextColors.GREEN, "[XManager] | Successfully set the hub location!"));

    }

}
