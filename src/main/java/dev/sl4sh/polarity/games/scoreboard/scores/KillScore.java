package dev.sl4sh.polarity.games.scoreboard.scores;

import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.scoreboard.SpongeScore;

import java.util.Collections;
import java.util.Set;

public class KillScore extends SpongeScore {

    int kills = 1;
    Objective obj;

    public KillScore(Objective obj){
        super(Text.of("Kills"));

        this.obj = obj;

    }

    @Override
    public int getScore() {
        return kills;
    }

    @Override
    public void setScore(int score) {
        kills = score;
    }

    @Override
    public Set<Objective> getObjectives() {
        return Collections.singleton(obj);
    }

}
