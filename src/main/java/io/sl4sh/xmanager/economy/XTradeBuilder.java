package io.sl4sh.xmanager.economy;

import io.sl4sh.xmanager.XManager;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;

import java.util.Optional;

@ConfigSerializable
public class XTradeBuilder {

    @Setting(value = "tradeName")
    public String tradeName = "";
    @Setting(value = "firstItem")
    public ItemStackSnapshot firstBuyingItem = ItemStackSnapshot.NONE;
    @Setting(value = "secondItem")
    public ItemStackSnapshot secondBuyingItem = ItemStackSnapshot.NONE;
    @Setting(value = "sellingItem")
    public ItemStackSnapshot sellingItem = ItemStackSnapshot.NONE;

    public XTradeBuilder() {}

    public XTradeBuilder(String tradeName, ItemStackSnapshot firstBuyingItem, ItemStackSnapshot secondBuyingItem, ItemStackSnapshot sellingItem) {

        this.tradeName = tradeName;
        this.firstBuyingItem = firstBuyingItem;
        this.secondBuyingItem = secondBuyingItem;
        this.sellingItem = sellingItem;

    }

    public void saveTradeBuilder(){

        XManager.getXManager().getTradesContainer().getTradeList().add(this);
        XManager.getXManager().writeCustomTrades();

    }

    public Optional<TradeOffer> makeTradeOffer(){

        if(firstBuyingItem != ItemStackSnapshot.NONE && sellingItem != ItemStackSnapshot.NONE){

            TradeOffer offer = TradeOffer.builder().firstBuyingItem(firstBuyingItem.createStack()).secondBuyingItem(secondBuyingItem.createStack()).maxUses(1000000000).sellingItem(sellingItem.createStack()).canGrantExperience(false).build();

            return Optional.of(offer);

        }

        return Optional.empty();

    }



}
