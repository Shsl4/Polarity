package io.sl4sh.xmanager.factions;

public class XFactionAllyData {

    public String factionName;
    public XFactionPermissionData permissions;

    public String getFactionName(){

        return this.factionName;

    }

    public XFactionPermissionData getPermissions(){

        return this.permissions;

    }

    public void setFactionName(String factionName){

        this.factionName = factionName;

    }

    public void setPermissions(XFactionPermissionData permissions){

        this.permissions = permissions;

    }

    public void setXFactionAllyData(XFactionAllyData data){

        this.factionName = data.factionName;
        this.permissions = data.permissions;

    }

    public XFactionAllyData getXFactionAllyData(){

        return this;

    }

    public XFactionAllyData(){

    }

    public XFactionAllyData(String factionName, XFactionPermissionData permissions){

        this.factionName = factionName;
        this.permissions = permissions;

    }

}
