package io.sl4sh.xmanager.factions;

import java.util.ArrayList;
import java.util.List;

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
