package dev.sl4sh.polarity.data.registration.npcdata;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

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
