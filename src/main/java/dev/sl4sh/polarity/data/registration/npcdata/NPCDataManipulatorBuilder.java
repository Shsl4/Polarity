package dev.sl4sh.polarity.data.registration.npcdata;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import dev.sl4sh.polarity.enums.NPCTypes;
import dev.sl4sh.polarity.enums.PolarityColors;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.*;

public class NPCDataManipulatorBuilder implements DataManipulatorBuilder<NPCData, ImmutableNPCData> {

    @Override
    public NPCData create() {
        return new NPCData();
    }

    @Override
    public Optional<NPCData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(NPCData.class).orElse(new NPCData()));
    }

    @Override
    public Optional<NPCData> build(DataView container) throws InvalidDataException {

        return new NPCData().from(container);

    }

}
