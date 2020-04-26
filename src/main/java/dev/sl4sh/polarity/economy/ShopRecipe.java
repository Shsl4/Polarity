package dev.sl4sh.polarity.economy;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.enums.PolarityColors;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.util.Optional;

@ConfigSerializable
public class ShopRecipe extends AbstractDataBuilder<ShopRecipe> implements DataSerializable {

    @Setting(value = "index")
    private Integer index = -1;

    @Setting(value = "price")
    private Float price = 0.0f;

    @Setting(value = "targetItem")
    private ItemStackSnapshot targetItem = ItemStackSnapshot.NONE;

    public ShopRecipe() {
        super(ShopRecipe.class, 0);
    }

    public ShopRecipe(float price, ItemStackSnapshot targetItem, int index) {
        this();
        this.price = price;
        this.targetItem = targetItem;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ItemStackSnapshot getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(ItemStackSnapshot targetItem) {
        this.targetItem = targetItem;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {

        if(price < 0) { this.price = 0.0f; return;}

        this.price = price;
    }

    public boolean isValidRecipe(){

        return price > 0.0f && targetItem.getType() != ItemTypes.BARRIER && targetItem.getType() != ItemTypes.AIR;

    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Nonnull
    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew().set(DataQuery.of("RecipePrice"), this.price)
                .set(DataQuery.of("RecipeItem"), this.targetItem)
                .set(DataQuery.of("RecipeIndex"), this.index);
    }

    @Override
    protected Optional<ShopRecipe> buildContent(DataView container) throws InvalidDataException {

        this.price = container.getFloat(DataQuery.of("RecipePrice")).get();
        this.index = container.getInt(DataQuery.of("RecipeIndex")).get();
        this.targetItem = container.getSerializable(DataQuery.of("RecipeItem"), ItemStackSnapshot.class).get();

        return Optional.of(this);

    }

}
