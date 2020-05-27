package dev.sl4sh.polarity.data.registration.beddata;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class BedDataManipulatorBuilder implements DataManipulatorBuilder<BedData, ImmutableBedData> {

    @Override
    public BedData create() {
        return new BedData();
    }

    @Override
    public Optional<BedData> createFrom(DataHolder dataHolder) {
        return Optional.of(dataHolder.get(BedData.class).orElse(new BedData()));
    }

    @Override
    public Optional<BedData> build(DataView container) throws InvalidDataException {

        return Optional.empty();

    }

}
