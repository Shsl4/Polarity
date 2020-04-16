package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.data.InventoryBackup;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class InventoryBackupsContainer implements PolarityContainer<InventoryBackup> {

    @Setting(value = "list")
    @Nonnull
    private List<InventoryBackup> list = new ArrayList<>();

    @Nonnull
    @Override
    public List<InventoryBackup> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull InventoryBackup object) {

        for(InventoryBackup backup : this.list){

            if(backup.getTargetPlayerID().equals(object.getTargetPlayerID())){

                backup.getSnapshots().addAll(object.getSnapshots());
                return true;

            }

        }

        return this.list.add(object);

    }

    @Override
    public boolean remove(@Nonnull InventoryBackup object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() { return getList().size() > 0; }

    public InventoryBackupsContainer() {}

    public Optional<InventoryBackup> getBackupForPlayer(UUID playerID){

        for(InventoryBackup backup : this.getList()){

            if(backup.getTargetPlayerID().equals(playerID)){

                return Optional.of(backup);

            }

        }

        return Optional.empty();

    }

}
