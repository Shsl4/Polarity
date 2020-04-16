package dev.sl4sh.polarity.data.factions;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class FactionPermissionData {

    @Setting(value = "claim")
    private Boolean claim = false;

    @Setting(value = "interact")
    private Boolean interact = false;

    @Setting(value = "manage")
    private Boolean manage = false;

    public void setClaim(Boolean claim) {
        this.claim = claim;
    }

    public Boolean getClaim() {
        return claim;
    }

    public Boolean getManage() {
        return manage;
    }

    public Boolean getInteract() {
        return interact;
    }

    public void setManage(Boolean manage) {
        this.manage = manage;
    }

    public void setInteract(Boolean interact) {
        this.interact = interact;
    }

    public FactionPermissionData(){

    }

    public FactionPermissionData(Boolean claim, Boolean interact, Boolean configure){

        this.claim = claim;
        this.interact = interact;
        this.manage = configure;

    }

}
