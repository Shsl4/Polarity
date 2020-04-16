package dev.sl4sh.polarity.data.containers;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

public interface PolarityContainer<T extends Serializable> {

    @Nonnull
    List<T> getList();

    boolean add(@Nonnull T object);

    boolean remove(@Nonnull T object);

    boolean shouldSave();

}
