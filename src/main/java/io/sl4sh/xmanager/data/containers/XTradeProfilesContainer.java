package io.sl4sh.xmanager.data.containers;

import io.sl4sh.xmanager.economy.XShopProfile;
import io.sl4sh.xmanager.economy.XTradeProfile;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigSerializable
public class XTradeProfilesContainer {

    @Setting(value = "profilesList")
    private List<XTradeProfile> tradeProfiles = new ArrayList<>();

    public Optional<XTradeProfile> getTradeProfileByName(String profileName){

        for(XTradeProfile profile : tradeProfiles){

            if(profile.getProfileName().equals(profileName)){

                return Optional.of(profile);

            }

        }

        return Optional.empty();

    }

    public void listTradesProfiles(CommandSource src){

        src.sendMessage(Text.of(TextColors.DARK_GREEN, "============ Shop list ============"));

        if(tradeProfiles.size() <= 0) { src.sendMessage(Text.of(TextColors.GREEN, "Nothing to see here... Yet!")); return; }

        int it = 1;

        for(XTradeProfile tradeProfile : tradeProfiles){

            src.sendMessage(Text.of(TextColors.GREEN, "#" , it , ". " , TextColors.WHITE , tradeProfile.getProfileName()));

            it++;

        }

    }

    public XTradeProfilesContainer() {}


    public List<XTradeProfile> getTradeProfiles() {
        return tradeProfiles;
    }

    public void setTradeProfiles(List<XTradeProfile> tradeProfiles) {
        this.tradeProfiles = tradeProfiles;
    }
}
