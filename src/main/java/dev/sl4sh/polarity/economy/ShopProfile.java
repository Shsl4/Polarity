package dev.sl4sh.polarity.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackComparators;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

@ConfigSerializable
public class ShopProfile extends AbstractDataBuilder<ShopProfile> implements DataSerializable, Serializable {

    @Nonnull
    @Setting(value = "shopRecipes")
    private List<ShopRecipe> shopRecipes = new ArrayList<>();

    @Nonnull
    @Setting(value = "profileName")
    private String profileName = "";

    @Setting(value = "shopPageHeight")
    private Integer shopPageHeight = 3;

    public ShopProfile() {
        super(ShopProfile.class, 0);
    }

    public ShopProfile(List<ShopRecipe> shopRecipes, String profileName, int shopPageHeight) {
        super(ShopProfile.class, 0);

        this.shopRecipes = shopRecipes;
        this.profileName = profileName;
        this.shopPageHeight = shopPageHeight;

    }

    public Optional<ShopRecipe> getRecipeWithIndex(int index){

        for(ShopRecipe recipe : shopRecipes){

            if(recipe.getIndex() == index){

                return Optional.of(recipe);

            }

        }

        return Optional.empty();

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

            if(ItemStackComparators.TYPE.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    ItemStackComparators.PROPERTIES.compare(recipe.getTargetItem().createStack(), editedSnap) == 0 &&
                    snapVal == testVal){

                return Optional.of(recipe);

            }

        }

        return Optional.empty();

    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew().set(DataQuery.of("RecipeValues"), this.shopRecipes)
                .set(DataQuery.of("ProfileName"), this.profileName)
                .set(DataQuery.of("PageHeight"), this.shopPageHeight);
    }


    @Override
    public Optional<ShopProfile> buildContent(DataView container) throws InvalidDataException {

        this.shopPageHeight = container.getInt(DataQuery.of("PageHeight")).get();
        this.profileName = container.getString(DataQuery.of("ProfileName")).get();
        this.shopRecipes = new ArrayList<>();

        for(Object object : container.getList(DataQuery.of("RecipeValues")).get()){

            DataView view = (DataView)object;
            this.shopRecipes.add(new ShopRecipe().buildContent(view).get());

        }

        return Optional.of(this);

    }
}
