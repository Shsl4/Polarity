package io.sl4sh.xmanager.data.factions;

public class XFactionPermissionData {

    private Boolean claim = false;
    private Boolean interact = false;
    private Boolean configure = false;

    public void setClaim(Boolean claim) {
        this.claim = claim;
    }

    public Boolean getClaim() {
        return claim;
    }

    public Boolean getConfigure() {
        return configure;
    }

    public Boolean getInteract() {
        return interact;
    }

    public void setConfigure(Boolean configure) {
        this.configure = configure;
    }

    public void setInteract(Boolean interact) {
        this.interact = interact;
    }

    public XFactionPermissionData(){

    }

    public XFactionPermissionData(Boolean claim, Boolean interact, Boolean configure){

        this.claim = claim;
        this.interact = interact;
        this.configure = configure;

    }

}
