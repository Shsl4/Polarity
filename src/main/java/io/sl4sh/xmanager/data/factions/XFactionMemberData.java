package io.sl4sh.xmanager.data.factions;

public class XFactionMemberData {

    public String playerName;
    public XFactionPermissionData permissions;

    public XFactionPermissionData getPermissions() {
        return permissions;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPermissions(XFactionPermissionData permissions) {
        this.permissions = permissions;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public XFactionMemberData(){


    }

    public XFactionMemberData(String playerName, XFactionPermissionData permissions){

        this.playerName = playerName;
        this.permissions = permissions;

    }

}
