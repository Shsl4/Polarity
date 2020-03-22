package io.sl4sh.xmanager.player;

import io.sl4sh.xmanager.XHomeData;
import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class XPlayerContainer {

    private List<XPlayer> players = new ArrayList<XPlayer>();

    public List<XPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<XPlayer> players) {
        this.players = players;
    }

    public void addPlayer(XPlayer ply){

        players.add(ply);

    }

    public boolean isPlayerRegistered(Player ply){

        if(!players.isEmpty()){

            for(XPlayer xPly : players){

                if(xPly.getPlayerName().equals(ply.getName())){

                    return true;

                }

            }

        }

        return false;

    }

    public XPlayer getXPlayerByPlayer(Player ply){

        for(XPlayer xPly : players){

            if(xPly.getPlayerName().equals(ply.getName())){

                return xPly;

            }

        }

        return null;

    }

    public boolean registerPlayer(Player ply){

        List<XHomeData> playerHomes = new ArrayList<XHomeData>();

        players.add(new XPlayer("", playerHomes, ply.getName()));

        return XManager.getXManager().writePlayerInfo();

    }

}
