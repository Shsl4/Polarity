package io.sl4sh.xmanager.enums;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public enum XColor {

    AQUA(TextColors.AQUA, "&b"),
    BLACK(TextColors.BLACK, "&0"),
    BLUE(TextColors.BLUE, "&9"),
    DARK_AQUA(TextColors.DARK_AQUA, "&3"),
    DARK_BLUE(TextColors.DARK_BLUE, "&1"),
    DARK_GRAY(TextColors.DARK_GRAY, "&8"),
    DARK_GREEN(TextColors.DARK_GREEN, "&2"),
    DARK_PURPLE(TextColors.DARK_PURPLE, "&5"),
    DARK_RED(TextColors.DARK_RED, "&4"),
    GOLD(TextColors.GOLD, "&6"),
    GRAY(TextColors.GRAY, "&7"),
    GREEN(TextColors.GREEN, "&a"),
    LIGHT_PURPLE(TextColors.LIGHT_PURPLE, "&d"),
    RED(TextColors.RED, "&c"),
    WHITE(TextColors.WHITE, "&f"),
    YELLOW(TextColors.YELLOW, "&e");

    private TextColor textColor;
    private String stringColor;


    XColor(TextColor textColor, String stringColor) {

        this.textColor = textColor;
        this.stringColor = stringColor;

    }

    public TextColor getTextColor(){

        return textColor;

    }

    public String getStringColor(){

        return stringColor;

    }

}
