package io.sl4sh.xmanager.factions;

import com.flowpowered.math.vector.Vector3i;

public class XFactionHomeData {

    public String homeName;
    public Vector3i homeLocation;

    public String getHomeName() {
        return homeName;
    }

    public Vector3i getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Vector3i homeLocation) {
        this.homeLocation = homeLocation;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public XFactionHomeData(){


    }

    public XFactionHomeData(String homeName, Vector3i homeLocation){

        this.homeName = homeName;
        this.homeLocation = homeLocation;

    }

}
