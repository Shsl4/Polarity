package io.sl4sh.xmanager.commands;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.data.registration.merchantdata.XMerchantData;
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

public class XManagerReloadShops implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Reload the shops and trade configuration"))
                .permission("xmanager.reloadshops")
                .executor(new XManagerReloadShops())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XManager plugin = XManager.getXManager();

        plugin.loadShopProfiles();

        for(World world : Sponge.getServer().getWorlds()){

            for(Entity entity : world.getEntities()){

                if(!entity.get(XMerchantData.class).isPresent()) { continue; }

                Optional<XShopProfile> optShopProfile = XManager.getShopProfiles().getShopProfileByName(entity.get(XMerchantData.class).get().merchantData().get());

                net.minecraft.entity.Entity mcEntity = (net.minecraft.entity.Entity)entity;

                ICustomNpc npc = (ICustomNpc) NpcAPI.Instance().getIEntity(mcEntity);

                if(!optShopProfile.isPresent()) { npc.getAdvanced().setLine(0, 0, "I am currently unavailable", ""); continue; }

                XShopProfile shopProfile = optShopProfile.get();


            }

        }

        src.sendMessage(Text.of(TextColors.AQUA, "[XManager] | Reloaded trades and shop profiles."));

        return CommandResult.success();

    }
}
