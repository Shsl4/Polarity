package io.sl4sh.xmanager.data;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class XHomeData {

    private String homeName;
    private String homeLocation;
    private String dimensionName;

    public XHomeData(String homeName, String homeLocation, String dimensionName){

        this.homeName = homeName;
        this.homeLocation = homeLocation;
        this.dimensionName = dimensionName;

    }

    public XHomeData(){


    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

}
