package io.sl4sh.xmanager.player;

import io.sl4sh.xmanager.XHomeData;

import java.util.ArrayList;
import java.util.List;

public class XPlayer {

    private String playerName;
    private String playerFaction;
    private List<XHomeData> playerHomes;

    public XPlayer(){

    }

    public void addHome(XHomeData newHome){

        playerHomes.add(newHome);

    }


    public XPlayer(String playerFaction, List<XHomeData> playerHomes, String playerName){

        this.playerFaction = playerFaction;
        this.playerHomes = playerHomes;
        this.playerName = playerName;

    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<XHomeData> getPlayerHomes() {
        return playerHomes;
    }

    public void setPlayerHomes(List<XHomeData> playerHomes) {
        this.playerHomes = playerHomes;
    }

    public String getPlayerFaction() {
        return playerFaction;
    }

    public void setPlayerFaction(String playerFaction) {
        this.playerFaction = playerFaction;
    }
}
