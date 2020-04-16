package dev.sl4sh.polarity.data.factions;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.UUID;


@ConfigSerializable
public class FactionMemberData {

    @Nonnull
    @Setting(value = "playerUUID")
    public UUID playerUUID = UUID.randomUUID();

    @Nonnull
    @Setting(value = "permissions")
    public FactionPermissionData permissions = new FactionPermissionData();

    public FactionPermissionData getPermissions() {
        return permissions;
    }

    @Nonnull
    public UUID getPlayerUniqueID() {
        return this.playerUUID;
    }

    public void setPermissions(@Nonnull FactionPermissionData permissions) {
        this.permissions = permissions;
    }

    public void setPlayerUUID(@Nonnull UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public FactionMemberData(){


    }

    public FactionMemberData(@Nonnull UUID playerUUID, @Nonnull FactionPermissionData permissions){

        this.playerUUID = playerUUID;
        this.permissions = permissions;

    }

}
