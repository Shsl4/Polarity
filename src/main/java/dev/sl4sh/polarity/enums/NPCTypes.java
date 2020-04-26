package dev.sl4sh.polarity.enums;

import dev.sl4sh.polarity.Polarity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;

public enum NPCTypes implements DataSerializable {

    DEFAULT,
    ADMINSHOP_NPC,
    USERSHOP_NPC,
    BUYER_NPC,
    GAME_SELECTION_NPC;

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew().set(DataQuery.of("NPCType"), this.name());
    }
}
