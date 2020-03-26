package io.sl4sh.xmanager.data.factions;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;


@ConfigSerializable
public class XFactionMemberData {

    @Nonnull
    @Setting(value = "playerName")
    public String playerName = "";

    @Nonnull
    @Setting(value = "permissions")
    public XFactionPermissionData permissions = new XFactionPermissionData();

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
