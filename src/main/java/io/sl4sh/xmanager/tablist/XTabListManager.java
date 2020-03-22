package io.sl4sh.xmanager.tablist;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.commands.XFactionCommandManager;
import io.sl4sh.xmanager.factions.XFaction;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XTabListManager {

    public static void refreshTabLists(){

        XManager.xLogWarning("Refreshing TabLists...");

        for(Player tPlayer : Sponge.getServer().getOnlinePlayers()){

            TabList tPlayerTabList = tPlayer.getTabList();
            Optional<XFaction> optTargetPlayerFaction = XFactionCommandManager.getPlayerFaction(tPlayer);
            tPlayerTabList.setHeader(Text.of("  \u00a7b\u00A7kl\u00a7b Welcome to \u00A7d\u00A7lFactions\u00A7b " + tPlayer.getName() + "\u00a7b! \u00A7kl"));

            if(optTargetPlayerFaction.isPresent()){

                String niceDisplayName = optTargetPlayerFaction.get().getFactionDisplayName().replace("&", "\u00a7");
                tPlayerTabList.setFooter(Text.of("\u00a7b Your current faction is " + niceDisplayName + " "));

            }
            else{

                tPlayerTabList.setFooter(Text.of(" \u00a7bYou currently do not have any faction "));

            }

            for(Player iPlayer : Sponge.getServer().getOnlinePlayers()){

                Optional<TabListEntry> opIPlayerEntry = tPlayerTabList.getEntry(iPlayer.getUniqueId());

                if(opIPlayerEntry.isPresent()){

                    TabListEntry iPlayerEntry = opIPlayerEntry.get();
                    Optional<XFaction> optIPlayerFaction = XFactionCommandManager.getPlayerFaction(iPlayer);

                    if(optIPlayerFaction.isPresent()){

                        if(!optIPlayerFaction.get().getFactionPrefix().equals("")){

                            String iPlyFacPrefix = optIPlayerFaction.get().getFactionPrefix().replace("&", "\u00a7");
                            iPlayerEntry.setDisplayName(Text.of(iPlyFacPrefix + "\u00A7r " + iPlayer.getName()));

                        }
                        else{

                            iPlayerEntry.setDisplayName(Text.of(iPlayer.getName()));

                        }

                    }
                    else{

                        iPlayerEntry.setDisplayName(Text.of(iPlayer.getName()));

                    }

                }
                else{

                    XManager.xLogError("Entry absent. UUID = " + iPlayer.getUniqueId());
                    XManager.xLogError(tPlayerTabList.getEntries().toString());
                    XManager.xLogError(tPlayerTabList.toString());

                }

            }

        }

        XManager.xLogSuccess("TabLists Refreshed.");

    }

}
