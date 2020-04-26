package dev.sl4sh.polarity.enums.UI;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;

public enum StackTypes implements DataSerializable {

    PLACEHOLDER,
    SHOP_STACK,
    NAVIGATION_BUTTON,
    EDITION_BUTTON,
    GAME_SELECTION_STACK,
    LOBBY_SELECTION_STACK,
    LOBBY_CREATE_STACK;

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew().set(DataQuery.of("StackTypeValue"), this.toString()) ;
    }
}
