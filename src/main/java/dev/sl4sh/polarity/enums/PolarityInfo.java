package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public enum PolarityInfo {

    XERROR_XFMEMBER("[Factions] | You are already a member of a faction."),
    XERROR_XFEMEMBER("[Factions] | This player is already a member of a faction."),
    XERROR_ALREADYALLIED("[Factions] | This faction is already allied with you."),
    XERROR_UNCLAIMEDCHUNK("[Factions] | This chunk is not claimed."),
    XERROR_NOTALLIED("[Factions] | This faction is not your ally.");

    private String desc;

    PolarityInfo(String desc) {

        this.desc = desc;

    }

    public Text getDesc(){

        return Text.of(TextColors.AQUA, desc);

    }

}
