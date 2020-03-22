package io.sl4sh.xmanager;

public enum XError {

    XERROR_XFNULL("\u00a7cThe Faction does not exist."),
    XERROR_XFMEMBER("\u00a7cYou are already a member of a faction."),
    XERROR_XFEMEMBER("\u00a7cThis player is already a member of a faction."),
    XERROR_NOXF("\u00a7cYou do not have any faction."),
    XERROR_NOTAUTHORIZED("\u00a7cYou are not allowed to do this."),
    XERROR_FILEREADFAIL("\u00a7cFailed to open config file."),
    XERROR_DIRWRITEFAIL("\u00a7cFailed to create directory."),
    XERROR_FILEWRITEFAIL("\u00a7cFailed to write config file"),
    XERROR_CHUNKCLAIMED("\u00a7cThis chunk is already claimed."),
    XERROR_NONADJCHUNK("\u00a7cA chunk can only be claimed if it is located next to another claimed chunk."),
    XERROR_UNCLAIMEDCHUNK("\u00a7cThis chunk is not claimed."),
    XERROR_PLAYERCOMMAND("\u00a7cThis command can only be executed by players."),
    XERROR_SERVERCOMMAND("\u00a7cThis command can only be executed by the server."),
    XERROR_HNAMEEXISTS("\u00a7cA home already exist with this name."),
    XERROR_NOSAFELOC("\u00a7cUnable to find a safe location for this home. Make sure it is not obstructed or placed in the air."),
    XERROR_NOHOME("\u00a7cThis home has not been set."),
    XERROR_WRONGDIM("\u00a7cThis home is located in another dimension."),
    XERROR_LGPREFIX("\u00a7cA faction prefix may not contain more than 15 characters."),
    XERROR_INVALIDPERM("\u00a7cInvalid permission name provided. Valid permissions are: claim, interact and configure."),
    XERROR_NOTAMEMBER("\u00a7cThis player is not a member of your faction."),
    XERROR_NULLPLAYER("\u00a7cThis player does not exist."),
    XERROR_NOTINVITED("\u00a7cYou are not invited to this faction."),
    XERROR_NOCLAIMS("\u00a7cYour faction does not have any claims."),
    XERROR_NOALLYRQ("\u00a7cThis faction did not submit any alliance request to you."),
    XERROR_NOTALLIED("\u00a7cThis faction is not your ally."),
    XERROR_ALREADYALLIED("\u00a7bThis faction is already allied with you."),
    XERROR_UNKNOWN("\u00a7cFailed to execute task. Please try again.");

    private String desc;

    XError(String desc) {

        this.desc = desc;

    }

    public String getDesc(){

        return desc;

    }
}
