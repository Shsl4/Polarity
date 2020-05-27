package dev.sl4sh.polarity.games;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.*;

public class SessionProperties {

    private final int minPlayers;
    private final int maxPlayers;
    private final int profileID;
    private final String lobbyMapName;
    private final List<String> gameMapNames;
    private final String sessionDisplayName;
    private final int maxTeamPlayers;
    private final boolean pvp;
    private final GameMode spectatorMode;

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getLobbyMapName() {
        return lobbyMapName;
    }

    public List<String> getGameMapNames() {
        return gameMapNames;
    }

    public String getSessionDisplayName() {
        return sessionDisplayName;
    }

    public boolean getPVP() {
        return pvp;
    }

    public SessionProperties(int minPlayers, int maxPlayers, int profileID, String lobbyMapName, List<String> gameMapNames, String sessionDisplayName, int maxTeamPlayers, boolean pvp, GameMode spectatorMode) {

        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.profileID = profileID;
        this.lobbyMapName = lobbyMapName;
        this.gameMapNames = gameMapNames;
        this.sessionDisplayName = sessionDisplayName;
        this.maxTeamPlayers = maxTeamPlayers;
        this.pvp = pvp;
        this.spectatorMode = spectatorMode;
    }

    public static List<String> getValidLobbyNames(){

        return Arrays.asList("Lobby1", "Lobby2", "Lobby3");

    }

    public static String getRandomLobbyName(){

        Random rand = new Random();
        List<String> list = getValidLobbyNames();
        return list.get(rand.nextInt(list.size()));

    }

    public int getProfileID() {
        return profileID;
    }

    private static List<String> getValidRushMapNames(){

        return Arrays.asList("Rush1", "Rush2", "Rush3");

    }

    public int getMaxTeamPlayers() {
        return maxTeamPlayers;
    }

    public GameMode getSpectatorMode() {
        return this.spectatorMode;
    }

    private static class Configurations{

        public SessionProperties SPLEEF_0 = new SessionProperties(2, 2, 0, getRandomLobbyName(), Arrays.asList("Spleef2P", "Spleef4P"), "2 Player", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_1 = new SessionProperties(2, 4, 1, getRandomLobbyName(), Arrays.asList("Spleef4P", "Spleef6P"), "2-4 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_2 = new SessionProperties(4, 6, 2, getRandomLobbyName(), Collections.singletonList("Spleef6P"), "4-6 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_3 = new SessionProperties(2, 2, 3, getRandomLobbyName(), Arrays.asList("Spleef2P", "Spleef4P"), "2 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_4 = new SessionProperties(2, 4, 4, getRandomLobbyName(), Arrays.asList("Spleef4P", "Spleef6P"), "2-4 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_5 = new SessionProperties(4, 6, 5, getRandomLobbyName(), Collections.singletonList("Spleef6P"), "4-6 Players", 0, false, GameModes.SPECTATOR);

        public SessionProperties ARENA_0 = new SessionProperties(2, 2, 0, getRandomLobbyName(), Collections.singletonList("Arena2P"), "Free For All (2 Player)", 0, true, GameModes.ADVENTURE);
        public SessionProperties ARENA_1 = new SessionProperties(3, 3, 1, getRandomLobbyName(), Collections.singletonList("Arena3P"), "Free For All (3 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_2 = new SessionProperties(4, 4, 2, getRandomLobbyName(), Collections.singletonList("Arena4P"), "Free For All (4 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_3 = new SessionProperties(2, 2, 3, getRandomLobbyName(), Collections.singletonList("Arena2P"), "Free For All (2 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_4 = new SessionProperties(5, 5, 4, getRandomLobbyName(), Collections.singletonList("Arena5P"), "Free For All (5 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_5 = new SessionProperties(6, 6, 5, getRandomLobbyName(), Collections.singletonList("Arena6P"), "Free For All (6 Players)", 0, true, GameModes.SPECTATOR);

        public SessionProperties RUSH_0 = new SessionProperties(3, 3, 0, getRandomLobbyName(), getValidRushMapNames(), "1v1 (4 Players)", 1, true, GameModes.SPECTATOR);
        public SessionProperties RUSH_1 = new SessionProperties(4, 4, 1, getRandomLobbyName(), getValidRushMapNames(), "2v2 (4 Players)", 2, true, GameModes.SPECTATOR);
        public SessionProperties RUSH_2 = new SessionProperties(6, 6, 2, getRandomLobbyName(), getValidRushMapNames(), "2v2 (6 Players)", 2, true, GameModes.SPECTATOR);
        public SessionProperties RUSH_3 = new SessionProperties(8, 8, 3, getRandomLobbyName(), getValidRushMapNames(), "2v2 (8 Players)", 2, true, GameModes.SPECTATOR);
        public SessionProperties RUSH_4 = new SessionProperties(8, 8, 4, getRandomLobbyName(), getValidRushMapNames(), "4v4 (8 Players)", 4, true, GameModes.SPECTATOR);
        public SessionProperties RUSH_5 = new SessionProperties(16, 16, 5, getRandomLobbyName(), getValidRushMapNames(), "4v4 (16 Players)", 4, true, GameModes.SPECTATOR);

        public SessionProperties SPLEEF_TEAM_0 = new SessionProperties(2, 2, 0, getRandomLobbyName(), Arrays.asList("Spleef2P", "Spleef4P"), "2 Player", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_TEAM_1 = new SessionProperties(2, 4, 1, getRandomLobbyName(), Arrays.asList("Spleef4P", "Spleef6P"), "2-4 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_TEAM_2 = new SessionProperties(4, 6, 2, getRandomLobbyName(), Collections.singletonList("Spleef6P"), "4-6 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_TEAM_3 = new SessionProperties(2, 2, 3, getRandomLobbyName(), Arrays.asList("Spleef2P", "Spleef4P"), "2 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_TEAM_4 = new SessionProperties(2, 4, 4, getRandomLobbyName(), Arrays.asList("Spleef4P", "Spleef6P"), "2-4 Players", 0, false, GameModes.SPECTATOR);
        public SessionProperties SPLEEF_TEAM_5 = new SessionProperties(4, 6, 5, getRandomLobbyName(), Collections.singletonList("Spleef6P"), "4-6 Players", 0, false, GameModes.SPECTATOR);

        public SessionProperties ARENA_TEAM_0 = new SessionProperties(2, 2, 0, getRandomLobbyName(), Collections.singletonList("Arena2P"), "Free For All (2 Player)", 0, true, GameModes.ADVENTURE);
        public SessionProperties ARENA_TEAM_1 = new SessionProperties(3, 3, 1, getRandomLobbyName(), Collections.singletonList("Arena3P"), "Free For All (3 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_TEAM_2 = new SessionProperties(4, 4, 2, getRandomLobbyName(), Collections.singletonList("Arena4P"), "Free For All (4 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_TEAM_3 = new SessionProperties(2, 2, 3, getRandomLobbyName(), Collections.singletonList("Arena2P"), "Free For All (2 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_TEAM_4 = new SessionProperties(5, 5, 4, getRandomLobbyName(), Collections.singletonList("Arena5P"), "Free For All (5 Players)", 0, true, GameModes.SPECTATOR);
        public SessionProperties ARENA_TEAM_5 = new SessionProperties(6, 6, 5, getRandomLobbyName(), Collections.singletonList("Arena6P"), "Free For All (6 Players)", 0, true, GameModes.SPECTATOR);


    }

    public static SessionProperties getGameProperties(int gameID, int pageID, int profileID){

        Configurations confs = new Configurations();

        if(gameID == 0){

            if(pageID == 0){

                if(profileID == 0) { return confs.SPLEEF_0; }
                if(profileID == 1) { return confs.SPLEEF_1; }
                if(profileID == 2) { return confs.SPLEEF_2; }
                if(profileID == 3) { return confs.SPLEEF_3; }
                if(profileID == 4) { return confs.SPLEEF_4; }
                if(profileID == 5) { return confs.SPLEEF_5; }

            }

            if(pageID == 1){

                if(profileID == 0) { return confs.SPLEEF_TEAM_0; }
                if(profileID == 1) { return confs.SPLEEF_TEAM_1; }
                if(profileID == 2) { return confs.SPLEEF_TEAM_2; }
                if(profileID == 3) { return confs.SPLEEF_TEAM_3; }
                if(profileID == 4) { return confs.SPLEEF_TEAM_4; }
                if(profileID == 5) { return confs.SPLEEF_TEAM_5; }

            }

        }

        if(gameID == 1){

            if(pageID == 0){

                if(profileID == 0) { return confs.ARENA_0; }
                if(profileID == 1) { return confs.ARENA_1; }
                if(profileID == 2) { return confs.ARENA_2; }
                if(profileID == 3) { return confs.ARENA_3; }
                if(profileID == 4) { return confs.ARENA_4; }
                if(profileID == 5) { return confs.ARENA_5; }

            }

            if(pageID == 1){

                if(profileID == 0) { return confs.ARENA_TEAM_0; }
                if(profileID == 1) { return confs.ARENA_TEAM_1; }
                if(profileID == 2) { return confs.ARENA_TEAM_2; }
                if(profileID == 3) { return confs.ARENA_TEAM_3; }
                if(profileID == 4) { return confs.ARENA_TEAM_4; }
                if(profileID == 5) { return confs.ARENA_TEAM_5; }

            }

        }

        if(gameID == 2){

            if(profileID == 0) { return confs.RUSH_0; }
            if(profileID == 1) { return confs.RUSH_1; }
            if(profileID == 2) { return confs.RUSH_2; }
            if(profileID == 3) { return confs.RUSH_3; }
            if(profileID == 4) { return confs.RUSH_4; }
            if(profileID == 5) { return confs.RUSH_5; }

        }

        return new SessionProperties(0, 0, profileID, "", new ArrayList<>(), "INVALID", 0, false, GameModes.SPECTATOR);

    }

}
