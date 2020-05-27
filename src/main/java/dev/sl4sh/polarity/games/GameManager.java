package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.PolarityColor;
import dev.sl4sh.polarity.games.arena.ArenaGameSession;
import dev.sl4sh.polarity.games.rush.RushGameSession;
import dev.sl4sh.polarity.games.spleef.SpleefGameSession;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class GameManager {

    @Nonnull
    private final List<GameSession<?>> sessions = new ArrayList<>();

    @Nonnull
    public List<GameSession<?>> getGameSessions() {
        return sessions;
    }

    void removeSession(GameSession<?> session){

        sessions.remove(session);
        Polarity.getNPCManager().refreshGameSelectionUIs();

    }

    public boolean doesSessionExistsByID(int id){

        for(GameSession<?> session: getGameSessions()){

            if(session.getSessionID() == id){

                return true;

            }

        }

        return false;

    }

    public Optional<GameSession<?>> getGameSessionByID(int id){

        for(GameSession<?> session: getGameSessions()){

            if(session.getSessionID() == id){

                return Optional.of(session);

            }

        }

        return Optional.empty();

    }

    public int getNextFreeSessionID(){

       List<Integer> existingIDs = new ArrayList<>();

        for(GameSession<?> session: getGameSessions()){

            existingIDs.add(session.getSessionID());

        }

        Collections.sort(existingIDs);

        int old = -1;

        for(Integer num : existingIDs){

            Polarity.getLogger().info(String.valueOf(num));

            if(old + 1 != num){

                return old + 1;

            }

            old = num;

        }

        return old + 1;

    }

    public List<GameSession<?>> getGameSessionsByGameID(int id){

        List<GameSession<?>> list = new ArrayList<>();

        for(GameSession<?> session : getGameSessions()){

            if(session.getGame().getGameID() == id){

                list.add(session);

            }

        }

        return list;

    }

    public List<Integer> getValidGameIDs(){

        List<Integer> list = Arrays.asList(0, 1, 2);
        return list;

    }

    public Optional<GameSession<?>> getPlayerSession(Player player){

        for(GameSession<?> session : getGameSessions()){

            if(session.getSessionPlayers().contains(player.getUniqueId())){

                return Optional.of(session);

            }

        }

        return Optional.empty();

    }

    <T extends GameSession<?>> Optional<Class<T>> getSessionClassByGameID(int id){

        if(id == 0){

            return Optional.of((Class<T>) SpleefGameSession.class);

        }

        if(id == 1){

            return Optional.of((Class<T>) ArenaGameSession.class);

        }

        if(id == 2){

            return Optional.of((Class<T>) RushGameSession.class);

        }

        return Optional.empty();

    }

    public Optional<GameSession<?>> createNewGameSession(int gameID, int sessionID, @Nonnull SessionProperties properties) {

        if(doesSessionExistsByID(sessionID)) {

            Polarity.getLogger().info(PolarityColor.RED.getStringColor() + "Tried to create a game session with an existing id.");
            return Optional.empty();

        }

        Optional<Class<GameSession<?>>> optClass = getSessionClassByGameID(gameID);

        if(optClass.isPresent()){

            GameSession<?> session;

            if(optClass.get().equals(SpleefGameSession.class)){

                try{

                    session = new SpleefGameSession(this, sessionID, properties);
                    sessions.add(session);
                    Polarity.getNPCManager().refreshGameSelectionUIs();
                    return Optional.of(session);

                }
                catch (IllegalStateException e){

                    System.out.println(PolarityColor.RED.getStringColor() + e.getMessage());
                    return Optional.empty();

                }

            }

            if(optClass.get().equals(ArenaGameSession.class)){

                try{

                    session = new ArenaGameSession(this, sessionID, properties);
                    sessions.add(session);
                    Polarity.getNPCManager().refreshGameSelectionUIs();
                    return Optional.of(session);

                }
                catch (IllegalStateException e){

                    System.out.println(PolarityColor.RED.getStringColor() + e.getMessage());
                    return Optional.empty();

                }

            }

            if(optClass.get().equals(RushGameSession.class)){

                try{

                    session = new RushGameSession(sessionID, properties);
                    sessions.add(session);
                    Polarity.getNPCManager().refreshGameSelectionUIs();
                    return Optional.of(session);

                }
                catch (IllegalStateException e){

                    System.out.println(PolarityColor.RED.getStringColor() + e.getMessage());
                    return Optional.empty();

                }

            }

        }

        return Optional.empty();

    }

}
