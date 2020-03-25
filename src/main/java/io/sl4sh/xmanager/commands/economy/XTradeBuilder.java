package io.sl4sh.xmanager.commands.economy;

import io.sl4sh.xmanager.XManager;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.merchant.TradeOffer;

import java.util.Optional;

public class XTradeBuilder {

    public ItemStackSnapshot firstBuyingItem = ItemStackSnapshot.NONE;
    public ItemStackSnapshot secondBuyingItem = ItemStackSnapshot.NONE;
    public ItemStackSnapshot sellingItem = ItemStackSnapshot.NONE;

    public XTradeBuilder() {}

    public XTradeBuilder(ItemStackSnapshot firstBuyingItem, ItemStackSnapshot secondBuyingItem, ItemStackSnapshot sellingItem) {
        this.firstBuyingItem = firstBuyingItem;
        this.secondBuyingItem = secondBuyingItem;
        this.sellingItem = sellingItem;
    }

    public Optional<TradeOffer> makeTradeOffer(){

        if(firstBuyingItem != ItemStackSnapshot.NONE && sellingItem != ItemStackSnapshot.NONE){

            TradeOffer offer = TradeOffer.builder().firstBuyingItem(firstBuyingItem.createStack()).secondBuyingItem(secondBuyingItem.createStack()).maxUses(1000000000).sellingItem(sellingItem.createStack()).canGrantExperience(false).build();

            if(!XManager.getXManager().getTradesList().contains(offer)){

                XManager.getXManager().getTradesList().add(offer);
                XManager.getXManager().writeCustomTrades();

            }

            return Optional.of(offer);

        }

        return Optional.empty();

    }



}
