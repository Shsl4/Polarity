package io.sl4sh.xmanager.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Careers;
import org.spongepowered.api.item.merchant.TradeOffer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XTradeProfile {

    @Nonnull
    @Setting(value = "tradeRecipes")
    private List<TradeOffer> tradeRecipes = new ArrayList<>();

    @Nonnull
    @Setting(value = "profileName")
    private String profileName = "";

    @Nonnull
    @Setting(value = "merchantName")
    private String merchantName = "\u00a7dTrader";

    @Nonnull
    @Setting(value = "villagerCareer")
    private Career villagerCareer = Careers.ARMORER;

    public XTradeProfile() {}

    public XTradeProfile(@Nonnull List<TradeOffer> tradeRecipes, @Nonnull String profileName, @Nonnull String merchantName, Career villagerCareer) {

        this.tradeRecipes = tradeRecipes;
        this.profileName = profileName;
        this.merchantName = merchantName;
        this.villagerCareer = villagerCareer;

    }

    @Nonnull
    public List<TradeOffer> getTradeRecipes() {
        return tradeRecipes;
    }

    public void setTradeRecipes(@Nonnull List<TradeOffer> tradeRecipes) {
        this.tradeRecipes = tradeRecipes;
    }

    @Nonnull
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(@Nonnull String profileName) {
        this.profileName = profileName;
    }

    @Nonnull
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(@Nonnull String merchantName) {
        this.merchantName = merchantName;
    }

    @Nonnull
    public Career getVillagerCareer() {
        return villagerCareer;
    }

    public void setVillagerCareer(@Nonnull Career villagerCareer) {
        this.villagerCareer = villagerCareer;
    }
}
