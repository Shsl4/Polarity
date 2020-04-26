package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public enum PolarityInfo {

    XERROR_XFMEMBER("You are already a member of a faction."),
    XERROR_XFEMEMBER("This player is already a member of a faction."),
    XERROR_ALREADYALLIED("This faction is already allied with you."),
    XERROR_UNCLAIMEDCHUNK("This chunk is not claimed."),
    XERROR_NOTALLIED("This faction is not your ally.");

    private String desc;

    PolarityInfo(String desc) {

        this.desc = desc;

    }

    public Text getDesc(){

        return Text.of(TextColors.AQUA, desc);

    }

}
