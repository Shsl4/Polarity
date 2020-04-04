package io.sl4sh.xmanager.data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XWorldsInfoContainer {

    @Nonnull
    @Setting(value = "worldsInfo")
    private List<XWorldInfo> worldsInfo = new ArrayList<>();

    @Nonnull
    public List<XWorldInfo> getWorldsInfo() {
        return worldsInfo;
    }

    public void setWorldsInfo(@Nonnull List<XWorldInfo> worldsInfo) {
        this.worldsInfo = worldsInfo;
    }

}
