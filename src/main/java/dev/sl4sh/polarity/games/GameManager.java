package dev.sl4sh.polarity.games;

import dev.sl4sh.polarity.events.LobbyDestructionEvent;
import dev.sl4sh.polarity.events.GameDestructionEvent;
import dev.sl4sh.polarity.games.spleef.SpleefLobby;
import org.spongepowered.api.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameManager {

    @Nonnull
    private final List<GameLobbyBase<?>> gameLobbies = new ArrayList<>();

    @Nonnull
    private final List<GameBase> gameBases = new ArrayList<>();

    @Nonnull
    public List<GameBase> getGameBases() {
        return gameBases;
    }

    @Nonnull
    public List<GameLobbyBase<?>> getGameLobbies() {
        return gameLobbies;
    }

    public <Y extends GameLobbyBase<?>> Optional<GameLobbyBase<?>> createNewGameInstance(Class<Y> lobbyClass, Integer maxPlayers) {

        if(lobbyClass.equals(SpleefLobby.class)){

            Optional<SpleefLobby> lobby = SpleefLobby.createSpleefLobby(maxPlayers);

            if(lobby.isPresent()) {

                getGameLobbies().add(lobby.get());
                getGameBases().add(lobby.get().getGame());
                return Optional.of(lobby.get());

            }

        }

        return Optional.empty();

    }

    @Listener
    public void onLobbyDestruction(LobbyDestructionEvent event){

        gameLobbies.remove(event.getTargetLobby());

    }

    @Listener
    public void onGameDestruction(GameDestructionEvent event){

        gameBases.remove(event.getTargetGame());

    }
    
}
