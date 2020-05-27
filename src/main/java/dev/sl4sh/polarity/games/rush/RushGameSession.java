package dev.sl4sh.polarity.games.rush;

import dev.sl4sh.polarity.games.AbstractGameSession;
import dev.sl4sh.polarity.games.SessionProperties;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import javax.annotation.Nonnull;
import java.util.Random;

public class RushGameSession extends AbstractGameSession<RushGameInstance> {

    public RushGameSession(int sessionID, @Nonnull SessionProperties properties) throws IllegalStateException {
        super(sessionID, properties);
    }

    /**
     * This method should create the game that will be fetched with {@link #getGame()} ()}.
     *
     * @return The created game
     * @throws IllegalStateException If a game construction error occurs
     */
    @Override
    public RushGameInstance createGame() throws IllegalStateException {
        return new RushGameInstance(this.getProperties().getGameMapNames().get(new Random().nextInt(this.getProperties().getGameMapNames().size())), this);
    }

    @Override
    protected void setupScoreboard() {
/*
        for(UUID playerID : getActivePlayers()){

            if(!Utilities.getPlayerByUniqueID(playerID).isPresent()) { continue; }

            Player player = Utilities.getPlayerByUniqueID(playerID).get();

            Objective killsObjective = Objective.builder()
                    .criterion(Criteria.DUMMY)
                    .displayName(Text.of("Total kills"))
                    .name("Kills")
                    .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                    .build();

            Team playerTeam = getScoreboard().getMemberTeam(Text.of(player.getName())).get();

            Objective teamDisplay = Objective.builder()
                    .criterion(Criteria.DUMMY)
                    .displayName(Text.of("   ", playerTeam.getColor(), playerTeam.getName(), "   "))
                    .name("Team")
                    .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                    .build();

            killsObjective.addScore(new KillScore(killsObjective));
            getScoreboard().addObjective(teamDisplay);
            getScoreboard().addObjective(killsObjective);
            getScoreboard().updateDisplaySlot(killsObjective, DisplaySlots.SIDEBAR);

            player.setScoreboard(getScoreboard());

        }
*/
    }

    /**
     * This method handles block break events. Cancels everything be default.
     *
     * @param event       The event
     * @param eventPlayer The player who broke the block
     */
    @Override
    public void onBlockBreak(ChangeBlockEvent.Break event, Player eventPlayer) {
    }



}
