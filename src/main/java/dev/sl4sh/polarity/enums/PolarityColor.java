package dev.sl4sh.polarity.enums;

import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;

public enum PolarityColor {

    AQUA(Color.CYAN, TextColors.AQUA, DyeColors.CYAN,"\u00a7b", "Aqua"),
    BLACK(Color.BLACK, TextColors.BLACK, DyeColors.BLACK, "\u00a70", "Black"),
    BLUE(Color.BLUE, TextColors.BLUE, DyeColors.LIGHT_BLUE, "\u00a79", "Blue"),
    DARK_AQUA(Color.DARK_CYAN, TextColors.DARK_AQUA, DyeColors.CYAN, "\u00a73", "Dark Aqua"),
    DARK_BLUE(Color.ofRgb(0, 0, 170), TextColors.DARK_BLUE, DyeColors.BLUE, "\u00a71", "Dark Blue"),
    DARK_GRAY(Color.GRAY, TextColors.DARK_GRAY, DyeColors.GRAY, "\u00a78", "Dark Gray"),
    DARK_GREEN(Color.GREEN, TextColors.DARK_GREEN, DyeColors.GREEN, "\u00a72", "Dark Green"),
    DARK_PURPLE(Color.PURPLE, TextColors.DARK_PURPLE, DyeColors.PURPLE, "\u00a75", "Dark Purple"),
    // AA0000
    DARK_RED(Color.ofRgb(170, 0, 0), TextColors.DARK_RED, DyeColors.RED,"\u00a74", "Dark Red"),
    // FFAA00
    GOLD(Color.ofRgb(255, 170, 0), TextColors.GOLD, DyeColors.ORANGE,"\u00a76", "Gold"),
    GRAY(Color.GRAY, TextColors.GRAY, DyeColors.SILVER,"\u00a77", "Gray"),
    GREEN(Color.GREEN, TextColors.GREEN, DyeColors.LIME,"\u00a7a", "Green"),
    LIGHT_PURPLE(Color.MAGENTA, TextColors.LIGHT_PURPLE, DyeColors.MAGENTA,"\u00a7d", "Light Purple"),
    RED(Color.RED, TextColors.RED, DyeColors.RED,"\u00a7c", "Red"),
    WHITE(Color.WHITE, TextColors.WHITE, DyeColors.WHITE,"\u00a7f", "White"),
    YELLOW(Color.YELLOW, TextColors.YELLOW, DyeColors.YELLOW,"\u00a7e", "Yellow");

    private Color rawColor;
    private TextColor textColor;
    private DyeColor dyeColor;
    private String stringColor;
    private String colorName;

    public static String stringColorFrom(TextColor color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getTextColor().equals(color)){

                return pColor.getStringColor();

            }

        }

        return "";

    }

    public static DyeColor dyeColorFrom(TextColor color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getTextColor().equals(color)){

                return pColor.getDyeColor();

            }

        }

        return DyeColors.WHITE;

    }

    public static Color rawColorFrom(TextColor color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getTextColor().equals(color)){

                return pColor.getRawColor();

            }

        }

        return Color.WHITE;

    }

    public static TextColor textColorFrom(Color color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getRawColor().equals(color)){

                return pColor.getTextColor();

            }

        }

        return TextColors.NONE;

    }

    public static Text colorDisplayNameFrom(TextColor color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getTextColor().equals(color)){

                return pColor.getColorDisplayName();

            }

        }

        return Text.EMPTY;

    }

    public static String colorNameFrom(TextColor color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getTextColor().equals(color)){

                return pColor.getColorName();

            }

        }

        return "";

    }

    public static String colorNameFrom(Color color){

        for(PolarityColor pColor : PolarityColor.values()){

            if(pColor.getRawColor().equals(color)){

                return pColor.getColorName();

            }

        }

        return "";

    }

    PolarityColor(Color rawColor, TextColor textColor, DyeColor dyeColor, String stringColor, String colorName) {

        this.rawColor = rawColor;
        this.textColor = textColor;
        this.stringColor = stringColor;
        this.dyeColor = dyeColor;
        this.colorName = colorName;

    }

    public TextColor getTextColor(){

        return textColor;

    }

    public DyeColor getDyeColor(){

        return dyeColor;

    }

    public String getStringColor(){

        return stringColor;

    }

    public Color getRawColor(){

        return rawColor;

    }

    public String getColorName(){

        return this.colorName;

    }

    public Text getColorDisplayName(){

        return Text.of(this.textColor, this.colorName);

    }

}
