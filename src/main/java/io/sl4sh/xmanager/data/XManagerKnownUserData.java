package io.sl4sh.xmanager.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XManagerKnownUserData {

    private List<UUID> playersUUIDs = new ArrayList<>();

    public List<UUID> getPlayersUUIDs() {
        return playersUUIDs;
    }

    public void setPlayersUUIDs(List<UUID> playersUUIDs) {
        this.playersUUIDs = playersUUIDs;
    }

}
