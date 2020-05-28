package dev.sl4sh.polarity;

import dev.sl4sh.polarity.data.WorldInfo;
import dev.sl4sh.polarity.games.GameInstance;
import dev.sl4sh.polarity.games.GameSession;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Optional;

public class TabListManager {

    public TabListManager() {}

    public static void refreshForPlayer(Player player){

        Utilities.delayOneTick(() -> refresh(player));

    }

    public static void refreshForPlayers(List<Player> players){

        for(Player player : players){

            Utilities.delayOneTick(() -> refresh(player));

        }

    }

    public static void refreshAll(){

        for(Player player : Sponge.getServer().getOnlinePlayers()){

            Utilities.delayOneTick(() -> refresh(player));

        }

    }

    private static void refresh(Player target){

        for(Player tabPlayer : Sponge.getServer().getOnlinePlayers()){

            if(!target.getTabList().getEntry(tabPlayer.getUniqueId()).isPresent()){

                TabListEntry newEntry = TabListEntry.builder()
                        .gameMode(tabPlayer.get(Keys.GAME_MODE).get())
                        .displayName(Text.of(tabPlayer.getName()))
                        .latency(tabPlayer.getConnection().getLatency())
                        .list(target.getTabList())
                        .profile(tabPlayer.getProfile())
                        .build();

                target.getTabList().addEntry(newEntry);

            }

        }

        WorldInfo playerWorldInfo = Utilities.getOrCreateWorldInfo(target.getWorld());

        TabList playerTabList = target.getTabList();

        if(playerWorldInfo.isGameWorld() && Polarity.getGameManager().getPlayerSession(target).isPresent()){

            GameSession<?> playerSession = Polarity.getGameManager().getPlayerSession(target).get();
            playerTabList.setHeader(Text.of(TextColors.AQUA, "  ", TextStyles.OBFUSCATED, "l", TextStyles.RESET, TextColors.AQUA, " You are playing ", playerSession.getGame().getGameTintColor(), playerSession.getGame().getGameName(), " ", TextColors.AQUA, TextStyles.OBFUSCATED, "l", TextStyles.RESET, "  "));

            if (playerSession.getSpectatingPlayers().contains(target.getUniqueId())) {

                playerTabList.setFooter(Text.of(TextColors.GRAY, " You are currently spectating "));

            }
            else {

                Team playerTeam = playerSession.getPlayerTeam(target);

                if(playerTeam.equals(GameInstance.EMPTY_TEAM)){

                    playerTabList.setFooter(Text.of(playerSession.getGame().getGameTintColor(), " Your current team is undefined"));

                }
                else{

                    playerTabList.setFooter(Text.of(playerSession.getGame().getGameTintColor(), " Your current team is ", playerTeam.getColor(), playerTeam.getName(), " "));

                }

            }

        }
        else{

            playerTabList.setHeader(Text.of(TextColors.AQUA , "  ", TextStyles.OBFUSCATED, "l" , TextStyles.RESET , TextColors.AQUA , " Welcome to the " , TextColors.LIGHT_PURPLE , TextStyles.BOLD, "Server" , TextStyles.RESET , " ", TextColors.AQUA , target.getName() , "! ", TextStyles.OBFUSCATED, "l" , TextStyles.RESET , "  "));

            Optional<Faction> playerFaction = Utilities.getPlayerFaction(target);

            if(playerFaction.isPresent()){

                playerTabList.setFooter(Text.of(TextColors.AQUA , " Your current faction is " , playerFaction.get().getDisplayName() , TextColors.RESET , TextColors.AQUA , " "));

            }
            else{

                playerTabList.setFooter(Text.of(TextColors.AQUA , " You currently do not have any faction "));

            }

        }

        for(Player onlinePlayer : Sponge.getServer().getOnlinePlayers()){

            Optional<TabListEntry> optionalOnlinePlayerEntry = playerTabList.getEntry(onlinePlayer.getUniqueId());

            if(optionalOnlinePlayerEntry.isPresent()){

                TabListEntry onlinePlayerEntry = optionalOnlinePlayerEntry.get();
                Optional<Faction> onlinePlayerFaction = Utilities.getPlayerFaction(onlinePlayer);

                if(onlinePlayerFaction.isPresent()){

                    if(!onlinePlayerFaction.get().getPrefix().equals("")){

                        onlinePlayerEntry.setDisplayName(Text.of(onlinePlayerFaction.get().getPrefix() , TextColors.RESET, " " , onlinePlayer.getName()));

                    }
                    else{

                        onlinePlayerEntry.setDisplayName(Text.of(TextColors.WHITE, "[Faction] ", onlinePlayer.getName()));

                    }

                }
                else{

                    onlinePlayerEntry.setDisplayName(Text.of(TextColors.GREEN, "[Adventurer] ", TextColors.RESET, onlinePlayer.getName()));

                }

            }

        }

    }

}
