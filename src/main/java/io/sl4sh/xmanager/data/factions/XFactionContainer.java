package io.sl4sh.xmanager.data.factions;

import io.sl4sh.xmanager.XFaction;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XFactionContainer {

    @Nonnull
    @Setting(value = "factionsList")
    public List<XFaction> factionsList = new ArrayList<>();

    public List<XFaction> getFactionsList(){

        return this.factionsList;

    }

    public void setFactionsList(List<XFaction> factionsList){

        this.factionsList = factionsList;

    }

    public void add(XFaction faction){

        this.factionsList.add(faction);

    }

    public XFactionContainer(List<XFaction> factionList){

        this.factionsList = factionList;

    }

    public XFactionContainer(){

    }

}
