package io.sl4sh.xmanager.data;

public class XManagerLocationData {

    private String dimensionName = "";
    // Vector3d or Vector3i
    private String location = "";

    public XManagerLocationData(){

    }

    public XManagerLocationData(String dimensionName, String location){

        this.dimensionName = dimensionName;
        this.location = location;

    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
