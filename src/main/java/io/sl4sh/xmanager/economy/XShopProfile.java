package io.sl4sh.xmanager.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XShopProfile {

    @Nonnull
    @Setting(value = "shopRecipes")
    private List<XEconomyShopRecipe> shopRecipes = new ArrayList<>();

    @Nonnull
    @Setting(value = "profileName")
    private String profileName = "";

    @Nonnull
    @Setting(value = "shopName")
    private String shopName = "";

    @Nonnull
    @Setting(value = "merchantName")
    private String merchantName = "Merchant";

    @Setting(value = "shopPageHeight")
    private int shopPageHeight = 3;

    public XShopProfile() {}

    public XShopProfile(@Nonnull List<XEconomyShopRecipe> shopRecipes, @Nonnull String profileName, int shopPageHeight, @Nonnull String shopName, String merchantName){

        this.profileName = profileName;
        this.shopPageHeight = shopPageHeight;
        this.shopRecipes = shopRecipes;
        this.shopName = shopName;
        this.merchantName = merchantName;

    }

    @Nonnull
    public List<XEconomyShopRecipe> getShopRecipes() {
        return shopRecipes;
    }

    public void setShopRecipes(@Nonnull List<XEconomyShopRecipe> shopRecipes) {
        this.shopRecipes = shopRecipes;
    }

    public int getShopPageHeight() {
        return shopPageHeight;
    }

    public void setShopPageHeight(int shopPageHeight) {
        this.shopPageHeight = shopPageHeight;
    }

    @Nonnull
    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(@Nonnull String profileName) {
        this.profileName = profileName;
    }

    @Nonnull
    public String getShopName() {
        return shopName;
    }

    public void setShopName(@Nonnull String shopName) {
        this.shopName = shopName;
    }

    @Nonnull
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(@Nonnull String merchantName) {
        this.merchantName = merchantName;
    }
}
