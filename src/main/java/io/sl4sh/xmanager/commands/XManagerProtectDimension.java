package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.economy.XShopProfile;
import net.minecraft.entity.INpc;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XManagerProtectDimension implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Protects a dimension."))
                .permission("xmanager.protectdimension")
                .executor(new XManagerProtectDimension())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player caller = (Player)src;

            XManager.getConfigData().addProtectedDimension(caller.getWorld());
            caller.sendMessage(Text.of(TextColors.AQUA, "[XManager] | New dimension protected!"));

            XManager.getXManager().writeMainDataConfigurationFile();

        }

        return CommandResult.success();

    }

}
