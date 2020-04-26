package dev.sl4sh.polarity.data.registration.npcdata;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.enums.NPCTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImmutableNPCData extends AbstractImmutableData<ImmutableNPCData, NPCData> {

    private final List<String> tags;
    private final NPCTypes type;
    private final SharedUI sharedUI;
    private final ShopProfile shopProfile;
    private final List<ItemStackSnapshot> storage;

    public List<String> getTags() {
        return tags;
    }

    public NPCTypes getType() { return type; }

    public ShopProfile getShopProfile() {
        return shopProfile;
    }

    public List<ItemStackSnapshot> getStorage() {
        return storage;
    }

    public ImmutableNPCData(List<String> tags, NPCTypes type, SharedUI sharedUI, ShopProfile shopProfile, List<ItemStackSnapshot> storage) {
        this.tags = tags;
        this.type = type;
        this.sharedUI = sharedUI;
        this.shopProfile = shopProfile;
        this.storage = storage;
        registerGetters();
    }

    public ImmutableListValue<String> tags(){

        return Sponge.getRegistry().getValueFactory().createListValue(Polarity.Keys.NPC.TAGS, tags, new ArrayList<>()).asImmutable();
    }

    public ImmutableValue<NPCTypes> type(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.NPC.TYPE, this.type, NPCTypes.DEFAULT).asImmutable();

    }

    public ImmutableValue<Optional<SharedUI>> sharedUI(){

        return Sponge.getRegistry().getValueFactory().createOptionalValue(Polarity.Keys.NPC.SHARED_UI, this.sharedUI).asImmutable();

    }

    public ImmutableValue<ShopProfile> shopProfile(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.NPC.SHOP_PROFILE, this.shopProfile, new ShopProfile()).asImmutable();

    }

    public ImmutableListValue<ItemStackSnapshot> storage(){

        return Sponge.getRegistry().getValueFactory().createListValue(Polarity.Keys.NPC.STORAGE, this.storage).asImmutable();

    }

    @Override
    protected void registerGetters() {

        registerFieldGetter(Polarity.Keys.NPC.TAGS, this::getTags);
        registerKeyValue(Polarity.Keys.NPC.TAGS, this::tags);

        registerFieldGetter(Polarity.Keys.NPC.TYPE, this::getType);
        registerKeyValue(Polarity.Keys.NPC.TYPE, this::type);

        registerFieldGetter(Polarity.Keys.NPC.SHOP_PROFILE, this::getShopProfile);
        registerKeyValue(Polarity.Keys.NPC.SHOP_PROFILE, this::shopProfile);

        registerFieldGetter(Polarity.Keys.NPC.SHARED_UI, this::getSharedUI);
        registerKeyValue(Polarity.Keys.NPC.SHARED_UI, this::sharedUI);

        registerFieldGetter(Polarity.Keys.NPC.STORAGE, this::getStorage);
        registerKeyValue(Polarity.Keys.NPC.STORAGE, this::storage);

    }

    @Nonnull
    @Override
    public NPCData asMutable() {
        return new NPCData(this.tags, this.type, this.sharedUI, this.shopProfile, this.storage);
    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(Polarity.Keys.NPC.TAGS, this.tags)
                .set(Polarity.Keys.NPC.TYPE.getQuery(), this.type.name())
                .set(Polarity.Keys.NPC.SHOP_PROFILE.getQuery(), this.shopProfile)
                .set(Polarity.Keys.NPC.STORAGE.getQuery(), this.storage);

    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    public SharedUI getSharedUI() {
        return sharedUI;
    }
}
