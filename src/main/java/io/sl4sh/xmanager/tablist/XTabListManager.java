package io.sl4sh.xmanager.tablist;

import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XTabListManager {

    public static void refreshTabLists(){

        for(Player tPlayer : Sponge.getServer().getOnlinePlayers()){

            TabList tPlayerTabList = tPlayer.getTabList();
            Optional<XFaction> optTargetPlayerFaction = XUtilities.getPlayerFaction(tPlayer);
            tPlayerTabList.setHeader(Text.of(TextColors.AQUA , "  \u00A7kl" , TextColors.RESET , TextColors.AQUA , " Welcome to" , TextColors.LIGHT_PURPLE , " \u00A7lFactions " , TextColors.RESET , TextColors.AQUA , tPlayer.getName() , "! \u00A7kl" , TextColors.RESET , "  "));

            if(optTargetPlayerFaction.isPresent()){

                tPlayerTabList.setFooter(Text.of(TextColors.AQUA , " Your current faction is " , optTargetPlayerFaction.get().getFactionDisplayName() , TextColors.RESET , TextColors.AQUA , " "));

            }
            else{

                tPlayerTabList.setFooter(Text.of(TextColors.AQUA , " You currently do not have any faction "));

            }

            for(Player iPlayer : Sponge.getServer().getOnlinePlayers()){

                Optional<TabListEntry> opIPlayerEntry = tPlayerTabList.getEntry(iPlayer.getUniqueId());

                if(opIPlayerEntry.isPresent()){

                    TabListEntry iPlayerEntry = opIPlayerEntry.get();
                    Optional<XFaction> optIPlayerFaction = XUtilities.getPlayerFaction(iPlayer);

                    if(optIPlayerFaction.isPresent()){

                        if(!optIPlayerFaction.get().getFactionPrefix().equals("")){

                            iPlayerEntry.setDisplayName(Text.of(optIPlayerFaction.get().getFactionPrefix() , TextColors.RESET, " " , iPlayer.getName()));

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

                    // Entry Absent

                }

            }

        }

    }

}
