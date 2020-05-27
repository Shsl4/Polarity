package dev.sl4sh.polarity.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

@ConfigSerializable
public class ItemShopRecipe{

    @Setting(value = "index")
    private Integer index = -1;

    @Setting(value = "price")
    private ItemStackSnapshot price = ItemStackSnapshot.NONE;

    @Setting(value = "targetItem")
    private ItemStackSnapshot targetItem = ItemStackSnapshot.NONE;

    public ItemShopRecipe(ItemStackSnapshot price, ItemStackSnapshot targetItem, int index) {
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

    public ItemStackSnapshot getPrice() {
        return price;
    }

    public void setPrice(ItemStackSnapshot price) {

        if(price.equals(ItemStackSnapshot.NONE)) { return;}

        this.price = price;
    }

    public boolean isValidRecipe(){

        return !price.equals(ItemStackSnapshot.NONE);

    }

}
