package io.sl4sh.xmanager.data.factions;

import io.sl4sh.xmanager.XFaction;

import java.util.ArrayList;

public class XFactionContainer {

    public ArrayList<XFaction> factionList = new ArrayList<>();

    public ArrayList<XFaction> getFactionList(){

        return this.factionList;

    }

    public void setFactionList(ArrayList<XFaction> factionList){

        this.factionList = factionList;

    }

    public void addFaction(XFaction faction){

        this.factionList.add(faction);

    }

    public XFactionContainer(){


    }

}
