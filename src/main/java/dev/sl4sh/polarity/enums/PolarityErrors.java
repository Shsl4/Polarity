package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public enum PolarityErrors {

    PLAYERCOMMAND("This command can only be executed by players."),
    SERVERCOMMAND("This command can only be executed by the server."),
    NULLFACTION("The Faction does not exist."),
    NOFACTION("You do not have any faction."),
    UNAUTHORIZED("You are not allowed to do this."),
    NONADJCHUNK("A chunk can only be claimed if it is located next to another claimed chunk."),
    NOSAFELOC("Unable to find a safe location for this home. Make sure it is not obstructed or placed in the air."),
    NOHOME("This home has not been set."),
    LONGPREFIX("A faction prefix may not contain more than 15 characters."),
    FACTION_NOTAMEMBER("This player is not a member of your faction."),
    NULLPLAYER("This player does not exist."),
    FACTION_NOALLYREQUEST("This faction did not submit any alliance request to you."),
    PROTECTED("You can't claim a protected chunk."),
    UNKNOWN("Failed to execute task. Please try again.");

    private String desc;

    PolarityErrors(String desc) {

        this.desc = desc;

    }

    public Text getDesc(){

        return Text.of(TextColors.RED, desc);

    }

}
