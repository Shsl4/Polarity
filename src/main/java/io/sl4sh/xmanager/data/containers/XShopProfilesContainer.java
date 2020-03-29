package io.sl4sh.xmanager.data.containers;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.economy.XShopProfile;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public int getNumRegisteredProfiles() { return shopProfiles.size();}

}
