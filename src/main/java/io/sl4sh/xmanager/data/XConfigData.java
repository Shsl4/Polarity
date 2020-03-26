package io.sl4sh.xmanager.data;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XConfigData {

    @Setting(value = "serverProtectedChunks")
    private List<XManagerLocationData> serverProtectedChunks = new ArrayList<>();

    @Setting(value = "hubData")
    private XManagerLocationData hubData = new XManagerLocationData();

    public XConfigData() {


    }

    public List<XManagerLocationData> getServerProtectedChunks() {
        return serverProtectedChunks;
    }

    public void setServerProtectedChunks(List<XManagerLocationData> serverProtectedChunks) {
        this.serverProtectedChunks = serverProtectedChunks;
    }

    public XManagerLocationData getHubData() {
        return hubData;
    }

    public void setHubData(XManagerLocationData hubData) {
        this.hubData = hubData;
    }

}
