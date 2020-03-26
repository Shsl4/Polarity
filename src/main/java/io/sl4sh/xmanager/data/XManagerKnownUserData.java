package io.sl4sh.xmanager.data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigSerializable
public class XManagerKnownUserData {

    @Setting(value = "playerUUIDs")
    private List<UUID> playersUUIDs = new ArrayList<>();

    public List<UUID> getPlayersUUIDs() {
        return playersUUIDs;
    }

    public void setPlayersUUIDs(List<UUID> playersUUIDs) {
        this.playersUUIDs = playersUUIDs;
    }

}
