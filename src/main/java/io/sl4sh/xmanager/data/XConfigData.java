package io.sl4sh.xmanager.data;

import java.util.ArrayList;
import java.util.List;

public class XConfigData {

    private List<XManagerLocationData> serverProtectedChunks = new ArrayList<>();

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
