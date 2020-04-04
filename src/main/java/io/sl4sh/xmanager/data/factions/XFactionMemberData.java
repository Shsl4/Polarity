package io.sl4sh.xmanager.data.factions;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.UUID;


@ConfigSerializable
public class XFactionMemberData {

    @Nonnull
    @Setting(value = "playerUUID")
    public UUID playerUUID = UUID.randomUUID();

    @Nonnull
    @Setting(value = "permissions")
    public XFactionPermissionData permissions = new XFactionPermissionData();

    public XFactionPermissionData getPermissions() {
        return permissions;
    }

    @Nonnull
    public UUID getPlayerUniqueID() {
        return this.playerUUID;
    }

    public void setPermissions(@Nonnull XFactionPermissionData permissions) {
        this.permissions = permissions;
    }

    public void setPlayerUUID(@Nonnull UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public XFactionMemberData(){


    }

    public XFactionMemberData(@Nonnull UUID playerUUID, @Nonnull XFactionPermissionData permissions){

        this.playerUUID = playerUUID;
        this.permissions = permissions;

    }

}
