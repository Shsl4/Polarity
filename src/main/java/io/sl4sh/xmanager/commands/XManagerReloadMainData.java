package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.XMerchantData;
import io.sl4sh.xmanager.economy.XShopProfile;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.ICustomNpc;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class XManagerReloadMainData implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Reloads the main data configuration"))
                .permission("xmanager.reloadmaindata")
                .executor(new XManagerReloadMainData())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XManager plugin = XManager.getXManager();

        plugin.loadMainData();

        src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Reloaded Main data."));

        return CommandResult.success();

    }
}
