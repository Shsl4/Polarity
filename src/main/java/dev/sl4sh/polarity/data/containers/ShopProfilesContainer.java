package dev.sl4sh.polarity.data.containers;

import dev.sl4sh.polarity.economy.ShopProfile;
import dev.sl4sh.polarity.economy.ShopRecipe;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.*;

@ConfigSerializable
public class ShopProfilesContainer implements PolarityContainer<ShopProfile> {

    @Setting(value = "list")
    @Nonnull
    private List<ShopProfile> list = new ArrayList<>();

    @Nonnull
    @Override
    public List<ShopProfile> getList() {
        return list;
    }

    @Override
    public boolean add(@Nonnull ShopProfile object) {
        return list.add(object);
    }

    @Override
    public boolean remove(@Nonnull ShopProfile object) {
        return list.remove(object);
    }

    @Override
    public boolean shouldSave() { return getList().size() > 0; }

    public ShopProfilesContainer() {}

    public Optional<ShopProfile> getShopProfileByName(String profileName){

        for(ShopProfile profile : this.getList()){

            if(profile.getProfileName().equals(profileName)){

                return Optional.of(profile);

            }

        }

        return Optional.empty();

    }

    public void listShopProfiles(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Shop list ============"));

        if(this.getList().size() <= 0) { src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!")); return; }

        int it = 1;

        for(ShopProfile shopProfile : this.getList()){

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , shopProfile.getProfileName()));

            it++;

        }

    }

    public void addShopProfile(ShopProfile profile){

        removeProfileByName(profile.getProfileName());

        this.add(profile);

    }

    @Nonnull
    public Map<ItemStackSnapshot, Float> getSaleableItems(){

        Map<ItemStackSnapshot, Float> returnMap = new LinkedHashMap<>();

        for(ShopProfile shopProfile : this.getList()){

            for(ShopRecipe recipe : shopProfile.getShopRecipes()){

                returnMap.put(recipe.getTargetItem(), (recipe.getPrice() / recipe.getTargetItem().getQuantity()) / 2);

            }

        }

        return returnMap;

    }

    public float getSellPrice(ItemStack stack){

        return getSaleableItems().get(stack.createSnapshot()) == null ? 0.0f : getSaleableItems().get(stack.createSnapshot());

    }

    @Nonnull
    public List<String> getExistingShopProfilesNames(){

        List<String> targetList = new ArrayList<>();

        for(ShopProfile profile : this.getList()){

            targetList.add(profile.getProfileName());

        }

        return targetList;

    }

    public boolean removeProfileByName(String profileName){

        if(getShopProfileByName(profileName).isPresent()) { return this.remove(getShopProfileByName(profileName).get()); }

        return false;

    }

}
