package dev.sl4sh.polarity.enums.games;

public enum ChannelTypes {

    WORLD_CHANNEL("World Channel"),
    GENERAL_CHANNEL("General Channel"),
    FACTION_CHANNEL("Faction Channel");

    private final String displayName;

    ChannelTypes(String displayName){

        this.displayName = displayName;

    }

    public String getDisplayName() {
        return displayName;
    }
}
