package io.sl4sh.xmanager.data.containers;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XEconomyService;
import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XShopRecipe;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.*;

@ConfigSerializable
public class XShopProfilesContainer {

    @Nonnull
    @Setting(value = "shopProfiles")
    List<XShopProfile> shopProfiles = new ArrayList<>();

    public XShopProfilesContainer() {}

    public Optional<XShopProfile> getShopProfileByName(String profileName){

        for(XShopProfile profile : shopProfiles){

            if(profile.getProfileName().equals(profileName)){

                return Optional.of(profile);

            }

        }

        return Optional.empty();

    }

    public void listShopProfiles(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Shop list ============"));

        if(shopProfiles.size() <= 0) { src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!")); return; }

        int it = 1;

        for(XShopProfile shopProfile : shopProfiles){

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , shopProfile.getProfileName()));

            it++;

        }

    }

    public void addShopProfile(XShopProfile profile){

        removeProfileByName(profile.getProfileName());

        shopProfiles.add(profile);

    }

    @Nonnull
    public Map<ItemType, Float> getSellableItems(){

        Map<ItemType, Float> returnMap = new LinkedHashMap<>();

        for(XShopProfile shopProfile : shopProfiles){

            for(XShopRecipe recipe : shopProfile.getShopRecipes()){

                returnMap.put(recipe.getTargetItem().getType(), (recipe.getPrice() / recipe.getTargetItem().getQuantity()) / 2);

            }

        }

        return returnMap;

    }

    public float getSellPrice(ItemStack stack){

        return getSellableItems().get(stack.getType()) == null ? 0.0f : getSellableItems().get(stack.getType());

    }

    @Nonnull
    public List<String> getExistingShopProfilesNames(){

        List<String> targetList = new ArrayList<>();

        for(XShopProfile profile : shopProfiles){

            targetList.add(profile.getProfileName());

        }

        return targetList;

    }

    public boolean removeProfileByName(String profileName){

        if(getShopProfileByName(profileName).isPresent()) { return shopProfiles.remove(getShopProfileByName(profileName).get()); }

        return false;

    }

    public int getNumRegisteredProfiles() { return shopProfiles.size();}

}
