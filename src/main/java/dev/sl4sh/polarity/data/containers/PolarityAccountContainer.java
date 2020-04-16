package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.economy.PolarityAccount;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class PolarityAccountContainer implements PolarityContainer<PolarityAccount> {

    @Setting(value = "list")
    @Nonnull
    private List<PolarityAccount> list = new ArrayList<>();

    @Nonnull
    @Override
    public List<PolarityAccount> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull PolarityAccount object) {
        return list.add(object);
    }

    @Override
    public boolean remove(@Nonnull PolarityAccount object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() { return getList().size() > 0; }

    public PolarityAccountContainer() {}


}
