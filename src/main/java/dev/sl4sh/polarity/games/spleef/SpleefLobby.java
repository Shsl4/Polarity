package dev.sl4sh.polarity.games.spleef;

import dev.sl4sh.polarity.enums.PolarityColors;
import dev.sl4sh.polarity.games.AbstractGameLobby;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpleefLobby extends AbstractGameLobby<SpleefGame> {

    private List<Player> registeredPlayers = new ArrayList<>();

    public static Optional<SpleefLobby> createSpleefLobby(int maxPlayers){

        try{

            SpleefLobby lobby = new SpleefLobby(maxPlayers);
            return Optional.of(lobby);

        }
        catch (IllegalStateException e){

            System.out.println(PolarityColors.RED.getStringColor() + e.getMessage());
            return Optional.empty();

        }

    }

    public SpleefLobby(int maxPlayers) throws IllegalStateException {

        super("SpleefLobby", maxPlayers);

    }

    @Nonnull
    @Override
    protected SpleefGame createGame() throws IllegalStateException {

        return new SpleefGame("Spleef", getMaxPlayers());

    }

    @Nonnull
    @Override
    public List<Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    @Override
    public int getMinPlayersToStart() {
        return 1;
    }

    @Override
    public boolean registerPlayer(Player player) {

        if(isValidLobby() && getRegisteredPlayers().size() < getMaxPlayers()){

            return registeredPlayers.add(player);

        }

        return false;

    }

    @Override
    public boolean unRegisterPlayer(Player player) {
        return registeredPlayers.remove(player);
    }



}
