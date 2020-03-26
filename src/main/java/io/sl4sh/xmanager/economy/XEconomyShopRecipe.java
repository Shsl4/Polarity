package io.sl4sh.xmanager.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

@ConfigSerializable
public class XEconomyShopRecipe {

    @Setting(value = "price")
    private float price = 0.0f;

    @Setting(value = "targetItem")
    private ItemStackSnapshot targetItem = ItemStackSnapshot.NONE;

    public XEconomyShopRecipe() {}

    public XEconomyShopRecipe(float price, ItemStackSnapshot targetItem) {
        this.price = price;
        this.targetItem = targetItem;
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
        this.price = price;
    }

    public boolean isValidRecipe(){

        return price > 0.0f && targetItem != ItemStackSnapshot.NONE;

    }

}
