package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public enum PolarityErrors {

    XERROR_PLAYERCOMMAND("[Polarity] | This command can only be executed by players."),
    XERROR_SERVERCOMMAND("[Polarity] | This command can only be executed by the server."),
    XERROR_XFNULL("[Factions] | The Faction does not exist."),
    XERROR_NOXF("[Factions] | You do not have any faction."),
    XERROR_NOTAUTHORIZED("[Factions] | You are not allowed to do this."),
    XERROR_NONADJCHUNK("[Factions] | A chunk can only be claimed if it is located next to another claimed chunk."),
    XERROR_NOSAFELOC("[Factions] | Unable to find a safe location for this home. Make sure it is not obstructed or placed in the air."),
    XERROR_NOHOME("[Factions] | This home has not been set."),
    XERROR_LGPREFIX("[Factions] | A faction prefix may not contain more than 15 characters."),
    XERROR_NOTAMEMBER("[Factions] | This player is not a member of your faction."),
    XERROR_NULLPLAYER("[Factions] | This player does not exist."),
    XERROR_NOTINVITED("[Factions] | You are not invited to this faction."),
    XERROR_NOALLYRQ("[Factions] | This faction did not submit any alliance request to you."),
    XERROR_PROTECTED("[Factions] | You can't claim a protected chunk."),
    XERROR_UNKNOWN("[Factions] | Failed to execute task. Please try again.");

    private String desc;

    PolarityErrors(String desc) {

        this.desc = desc;

    }

    public Text getDesc(){

        return Text.of(TextColors.RED, desc);

    }

}
