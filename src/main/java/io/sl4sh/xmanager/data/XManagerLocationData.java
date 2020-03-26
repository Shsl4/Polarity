package io.sl4sh.xmanager.data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;

import java.io.Serializable;

@ConfigSerializable
public class XManagerLocationData {

    @Setting(value = "dimensionName")
    private String dimensionName = "";

    // Vector3d or Vector3i
    @Setting(value = "location")
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
