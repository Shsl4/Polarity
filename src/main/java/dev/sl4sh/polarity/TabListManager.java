package dev.sl4sh.polarity;

import dev.sl4sh.polarity.commands.PolarityWarp;
import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.Faction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.swing.*;
import java.util.Optional;

public class TabListManager {

    public TabListManager() {}

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event){

        if (!event.getTargetEntity().hasPlayedBefore()) {

            PolarityWarp.warp(event.getTargetEntity(), "Hub", Polarity.getPolarity());

            for (Player ply : Sponge.getGame().getServer().getOnlinePlayers()) {

                ply.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Please welcome ", TextColors.YELLOW, event.getTargetEntity().getName(), TextColors.LIGHT_PURPLE, " to the server!"));

            }

        }

        refreshTabLists();

    }


    @Listener
    public void onPlayerRespawn(RespawnPlayerEvent event){

        TabList playerTabList = event.getTargetEntity().getTabList();

        for(TabListEntry tlEntry : event.getOriginalPlayer().getTabList().getEntries()){

            TabListEntry nEntry = TabListEntry.builder()
                    .gameMode(tlEntry.getGameMode())
                    .displayName(tlEntry.getDisplayName().get())
                    .latency(tlEntry.getLatency())
                    .list(playerTabList)
                    .profile(tlEntry.getProfile())
                    .build();

            playerTabList.addEntry(nEntry);

        }

    }

    public static void refreshTabLists(){

        for(Player tPlayer : Sponge.getServer().getOnlinePlayers()){

            TabList tPlayerTabList = tPlayer.getTabList();
            Optional<Faction> optTargetPlayerFaction = Utilities.getPlayerFaction(tPlayer);
            tPlayerTabList.setHeader(Text.of(TextColors.AQUA , "  \u00A7kl" , TextColors.RESET , TextColors.AQUA , " Welcome to" , TextColors.LIGHT_PURPLE , " \u00A7lServer" , TextStyles.RESET , " ", TextColors.AQUA , tPlayer.getName() , "! \u00A7kl" , TextColors.RESET , "  "));

            if(optTargetPlayerFaction.isPresent()){

                tPlayerTabList.setFooter(Text.of(TextColors.AQUA , " Your current faction is " , optTargetPlayerFaction.get().getDisplayName() , TextColors.RESET , TextColors.AQUA , " "));

            }
            else{

                tPlayerTabList.setFooter(Text.of(TextColors.AQUA , " You currently do not have any faction "));

            }

            for(Player iPlayer : Sponge.getServer().getOnlinePlayers()){

                Optional<TabListEntry> opIPlayerEntry = tPlayerTabList.getEntry(iPlayer.getUniqueId());

                if(opIPlayerEntry.isPresent()){

                    TabListEntry iPlayerEntry = opIPlayerEntry.get();
                    Optional<Faction> optIPlayerFaction = Utilities.getPlayerFaction(iPlayer);

                    if(optIPlayerFaction.isPresent()){

                        if(!optIPlayerFaction.get().getPrefix().equals("")){

                            iPlayerEntry.setDisplayName(Text.of(optIPlayerFaction.get().getPrefix() , TextColors.RESET, " " , iPlayer.getName()));

                        }
                        else{

                            iPlayerEntry.setDisplayName(Text.of(TextColors.WHITE, "[Faction] ", iPlayer.getName()));

                        }

                    }
                    else{

                        iPlayerEntry.setDisplayName(Text.of(TextColors.GREEN, "[Adventurer] ", TextColors.RESET, iPlayer.getName()));

                    }

                }

            }

        }

    }

}
