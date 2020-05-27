package dev.sl4sh.polarity.data.registration.npcdata;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.UI.SharedUI;
import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.enums.NPCTypes;
import dev.sl4sh.polarity.enums.PolarityColor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NPCData extends AbstractData<NPCData, ImmutableNPCData> {

    private List<String> tags;
    private NPCTypes type;
    private SharedUI sharedUI;
    private ShopProfile shopProfile;
    private List<ItemStackSnapshot> storage;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public NPCTypes getType() { return type; }

    public void setType(NPCTypes type) { this.type = type; }

    public NPCData(){

        this(new ArrayList<>(), NPCTypes.DEFAULT, null, new ShopProfile(), new ArrayList<>());

    }

    public NPCData(List<String> tags, NPCTypes type, SharedUI sharedUI, ShopProfile shopProfile, List<ItemStackSnapshot> storage) {

        this.tags = tags;
        this.type = type;
        this.sharedUI = sharedUI;
        this.shopProfile = shopProfile;
        this.storage = storage;
        registerGettersAndSetters();

    }

    public ListValue<String> tags(){

        return Sponge.getRegistry().getValueFactory().createListValue(Polarity.Keys.NPC.TAGS, this.tags, new ArrayList<>());

    }

    public Value<NPCTypes> type(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.NPC.TYPE, this.type, NPCTypes.DEFAULT);

    }

    public OptionalValue<SharedUI> sharedUI(){

        return Sponge.getRegistry().getValueFactory().createOptionalValue(Polarity.Keys.NPC.SHARED_UI, this.sharedUI);

    }

    public Value<ShopProfile> shopProfile(){

        return Sponge.getRegistry().getValueFactory().createValue(Polarity.Keys.NPC.SHOP_PROFILE, this.shopProfile);

    }

    public ListValue<ItemStackSnapshot> storage(){

        return Sponge.getRegistry().getValueFactory().createListValue(Polarity.Keys.NPC.STORAGE, this.storage);

    }

    @Override
    protected void registerGettersAndSetters() {

        registerFieldGetter(Polarity.Keys.NPC.TAGS, this::getTags);
        registerFieldSetter(Polarity.Keys.NPC.TAGS, this::setTags);
        registerKeyValue(Polarity.Keys.NPC.TAGS, this::tags);

        registerFieldGetter(Polarity.Keys.NPC.TYPE, this::getType);
        registerFieldSetter(Polarity.Keys.NPC.TYPE, this::setType);
        registerKeyValue(Polarity.Keys.NPC.TYPE, this::type);

        registerFieldGetter(Polarity.Keys.NPC.SHOP_PROFILE, this::getShopProfile);
        registerFieldSetter(Polarity.Keys.NPC.SHOP_PROFILE, this::setShopProfile);
        registerKeyValue(Polarity.Keys.NPC.SHOP_PROFILE, this::shopProfile);

        registerFieldGetter(Polarity.Keys.NPC.SHARED_UI, this::getSharedUI);
        registerFieldSetter(Polarity.Keys.NPC.SHARED_UI, (value) -> setSharedUI(value.orElse(null)));
        registerKeyValue(Polarity.Keys.NPC.SHARED_UI, this::sharedUI);

        registerFieldGetter(Polarity.Keys.NPC.STORAGE, this::getStorage);
        registerFieldSetter(Polarity.Keys.NPC.STORAGE, this::setStorage);
        registerKeyValue(Polarity.Keys.NPC.STORAGE, this::storage);

    }

    public void setSharedUI(@Nullable SharedUI sharedUI) {
        this.sharedUI = sharedUI;
    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(Polarity.Keys.NPC.TAGS.getQuery(), this.tags)
                                    .set(Polarity.Keys.NPC.TYPE.getQuery(), this.type.name())
                                    .set(Polarity.Keys.NPC.STORAGE.getQuery(), this.storage)
                                    .set(Polarity.Keys.NPC.SHOP_PROFILE.getQuery(), this.shopProfile);
    }

    @Override
    public Optional<NPCData> fill(DataHolder dataHolder, MergeFunction overlap) {

        NPCData merged = overlap.merge(this, dataHolder.get(NPCData.class).orElse(new NPCData()));

        this.tags = (merged.tags().get());
        this.type = (merged.type().get());
        this.sharedUI = (merged.sharedUI().get()).orElse(null);
        this.shopProfile = merged.shopProfile().get();
        this.storage = merged.storage().get();

        return Optional.of(this);

    }


    @Override
    public Optional<NPCData> from(DataContainer container) {

        Polarity.getLogger().info(PolarityColor.AQUA.getStringColor() + "Building from data container");

        this.tags = container.getStringList(Polarity.Keys.NPC.TAGS.getQuery()).get();
        this.type = NPCTypes.valueOf(container.getString(Polarity.Keys.NPC.TYPE.getQuery()).orElse("DEFAULT"));
        this.shopProfile = new ShopProfile().buildContent((DataView)container.get(Polarity.Keys.NPC.SHOP_PROFILE.getQuery()).get()).get();
        this.storage = container.getSerializableList(Polarity.Keys.NPC.STORAGE.getQuery(), ItemStackSnapshot.class).orElse(new ArrayList<>());

        /*
        Polarity.getLogger().info(PolarityColor.GREEN.getStringColor() + "Success");
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "tag count: " + tags.size());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "type: " + type.name());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "profile recipes: " + shopProfile.getShopRecipes().size());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "storage count: " + storage.size());*/

        return Optional.of(this);

    }

    public Optional<NPCData> from(DataView view) {

        this.tags = view.getStringList(Polarity.Keys.NPC.TAGS.getQuery()).orElse(new ArrayList<>());
        this.type = NPCTypes.valueOf(view.getString(Polarity.Keys.NPC.TYPE.getQuery()).orElse("DEFAULT"));
        this.shopProfile = new ShopProfile().buildContent((DataView)view.get(Polarity.Keys.NPC.SHOP_PROFILE.getQuery()).get()).get();
        this.storage = view.getSerializableList(Polarity.Keys.NPC.STORAGE.getQuery(), ItemStackSnapshot.class).orElse(new ArrayList<>());

        /*
        Polarity.getLogger().info(PolarityColor.GREEN.getStringColor() + "Success");
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "tag count: " + tags.size());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "type: " + type.name());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "profile recipes: " + shopProfile.getShopRecipes().size());
        Polarity.getLogger().info(PolarityColor.LIGHT_PURPLE.getStringColor() + "storage count: " + storage.size());*/

        return Optional.of(this);

    }

    @Override
    public NPCData copy() {

        /*
        Polarity.getLogger().info(PolarityColor.GOLD.getStringColor() + "tag count: " + tags.size());
        Polarity.getLogger().info(PolarityColor.GOLD.getStringColor() + "type: " + type.name());
        Polarity.getLogger().info(PolarityColor.GOLD.getStringColor() + "profile recipes: " + shopProfile.getShopRecipes().size());
        Polarity.getLogger().info(PolarityColor.GOLD.getStringColor() + "storage count: " + storage.size());*/

        return new NPCData(this.tags, this.type, this.sharedUI, this.shopProfile, this.storage);

    }

    @Override
    public ImmutableNPCData asImmutable() {
        return new ImmutableNPCData(this.tags, this.type, this.sharedUI, this.shopProfile, this.storage);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    public Optional<SharedUI> getSharedUI() {
        return Optional.ofNullable(sharedUI);
    }

    public ShopProfile getShopProfile() {
        return shopProfile;
    }

    public void setShopProfile(ShopProfile shopProfile) {
        this.shopProfile = shopProfile;
    }

    public List<ItemStackSnapshot> getStorage() {
        return storage;
    }

    public void setStorage(List<ItemStackSnapshot> storage) {
        this.storage = storage;
    }

}
