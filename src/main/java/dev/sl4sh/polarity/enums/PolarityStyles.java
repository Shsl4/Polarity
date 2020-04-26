package dev.sl4sh.polarity.enums;

import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public enum PolarityStyles {

    NONE(TextStyles.NONE, ""),
    RESET(TextStyles.RESET, "\u00a7r"),
    BOLD(TextStyles.BOLD, "\u00a7l"),
    ITALIC(TextStyles.ITALIC, "\u00a7o"),
    OBFUSCATED(TextStyles.OBFUSCATED, "\u00a7k"),
    STRIKETHROUGH(TextStyles.STRIKETHROUGH, "\u00a7m"),
    UNDERLINE(TextStyles.UNDERLINE, "\u00a7n");

    private final TextStyle textStyle;
    private final String stringStyle;

    PolarityStyles(TextStyle textStyle, String stringStyle) {

        this.textStyle = textStyle;
        this.stringStyle = stringStyle;

    }

    public TextStyle getTextStyle(){

        return textStyle;

    }

    public String getStringStyle(){

        return stringStyle;

    }

}
