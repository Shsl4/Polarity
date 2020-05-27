package dev.sl4sh.polarity.games.spleef;

import dev.sl4sh.polarity.enums.games.GameSessionState;
import dev.sl4sh.polarity.games.AbstractGameSession;
import dev.sl4sh.polarity.games.GameManager;
import dev.sl4sh.polarity.games.SessionProperties;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Random;

public class SpleefGameSession extends AbstractGameSession<SpleefGameInstance> {

    public SpleefGameSession(GameManager gameManager, int sessionID, SessionProperties properties) throws IllegalStateException {
        super(sessionID, properties);
    }

    @Override
    public SpleefGameInstance createGame() throws IllegalStateException {
        return new SpleefGameInstance(this.getProperties().getGameMapNames().get(new Random().nextInt(this.getProperties().getGameMapNames().size())), this);
    }

    @Override
    protected void setupScoreboard() {

    }

    /**
     * This method handles block break events. Calls another method so functionality can be overridden by child classes (@Listener methods can't be overridden)
     *
     * @param event       The event
     * @param eventPlayer
     */
    @Override
    public void onBlockBreak(ChangeBlockEvent.Break event, Player eventPlayer) {

        if(getGame().isValidGame()){

            for(Transaction<BlockSnapshot> transaction : event.getTransactions()){

                BlockSnapshot snap = transaction.getOriginal();

                if(snap.getWorldUniqueId().equals(getGame().getGameWorld().get().getUniqueId())){

                    if(!getState().equals(GameSessionState.RUNNING)){

                        event.setCancelled(true);
                        return;
                    }

                    if(!snap.getState().getType().equals(BlockTypes.SNOW)){

                        event.setCancelled(true);

                    }

                }

            }

        }
    }
}
