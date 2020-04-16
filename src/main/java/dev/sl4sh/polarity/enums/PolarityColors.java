package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public enum PolarityColors {

    AQUA(TextColors.AQUA, "\u00a7b"),
    BLACK(TextColors.BLACK, "\u00a70"),
    BLUE(TextColors.BLUE, "\u00a79"),
    DARK_AQUA(TextColors.DARK_AQUA, "\u00a73"),
    DARK_BLUE(TextColors.DARK_BLUE, "\u00a71"),
    DARK_GRAY(TextColors.DARK_GRAY, "\u00a78"),
    DARK_GREEN(TextColors.DARK_GREEN, "\u00a72"),
    DARK_PURPLE(TextColors.DARK_PURPLE, "\u00a75"),
    DARK_RED(TextColors.DARK_RED, "\u00a74"),
    GOLD(TextColors.GOLD, "\u00a76"),
    GRAY(TextColors.GRAY, "\u00a77"),
    GREEN(TextColors.GREEN, "\u00a7a"),
    LIGHT_PURPLE(TextColors.LIGHT_PURPLE, "\u00a7d"),
    RED(TextColors.RED, "\u00a7c"),
    WHITE(TextColors.WHITE, "\u00a7f"),
    YELLOW(TextColors.YELLOW, "\u00a7e");

    private TextColor textColor;
    private String stringColor;

    PolarityColors(TextColor textColor, String stringColor) {

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
