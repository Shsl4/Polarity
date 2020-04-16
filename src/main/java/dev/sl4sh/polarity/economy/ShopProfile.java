package dev.sl4sh.polarity.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigSerializable
public class ShopProfile implements Serializable {

    @Nonnull
    @Setting(value = "shopRecipes")
    private List<ShopRecipe> shopRecipes = new ArrayList<>();

    @Nonnull
    @Setting(value = "profileName")
    private String profileName = "";

    @Setting(value = "shopPageHeight")
    private int shopPageHeight = 3;

    public ShopProfile() {}

    public ShopProfile(List<ShopRecipe> shopRecipes, String profileName, int shopPageHeight) {

        this.shopRecipes = shopRecipes;
        this.profileName = profileName;
        this.shopPageHeight = shopPageHeight;

    }

    @Nonnull
    public List<ShopRecipe> getShopRecipes() {
        return shopRecipes;
    }

    public void setShopRecipes(@Nonnull List<ShopRecipe> shopRecipes) {
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

    public Optional<ShopRecipe> getRecipeBySnapshot(ItemStackSnapshot snap){

        ItemStack editedSnap = snap.createStack();
        editedSnap.offer(Keys.ITEM_LORE, new ArrayList<>());

        for(ShopRecipe recipe : shopRecipes){

            DataContainer snapDamage = snap.toContainer();
            DataContainer testDamage = recipe.getTargetItem().toContainer();

            int snapVal = (int)snapDamage.get(DataQuery.of("UnsafeDamage")).get();
            int testVal = (int)testDamage.get(DataQuery.of("UnsafeDamage")).get();

            if(ItemStackComparators.TYPE_SIZE.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    ItemStackComparators.PROPERTIES.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    snapVal == testVal){

                return Optional.of(recipe);

            }

        }

        return Optional.empty();

    }

}
