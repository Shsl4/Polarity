package io.sl4sh.xmanager;

import java.util.ArrayList;
import java.util.List;

public class XConfigData {

    //Vector3i List
    private List<String> serverProtectedChunks = new ArrayList<>();
    //Vector3d
    private String hubLocation = "";


    public XConfigData() {

    }

    public List<String> getServerProtectedChunks() {
        return serverProtectedChunks;
    }

    public void setServerProtectedChunks(List<String> serverProtectedChunks) {
        this.serverProtectedChunks = serverProtectedChunks;
    }

    public String getHubLocation() {
        return hubLocation;
    }

    public void setHubLocation(String hubLocation) {
        this.hubLocation = hubLocation;
    }
}
