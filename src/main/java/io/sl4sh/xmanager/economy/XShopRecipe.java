package io.sl4sh.xmanager.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

@ConfigSerializable
public class XShopRecipe {

    @Setting(value = "price")
    private float price = 0.0f;

    @Setting(value = "targetItem")
    private ItemStackSnapshot targetItem = ItemStackSnapshot.NONE;

    public XShopRecipe() {}

    public XShopRecipe(float price, ItemStackSnapshot targetItem) {
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

        if(price < 0) { this.price = 0.0f; return;}

        this.price = price;
    }

    public boolean isValidRecipe(){

        return price > 0.0f && targetItem.getType() != ItemTypes.BARRIER && targetItem.getType() != ItemTypes.AIR;

    }

}
