package io.sl4sh.xmanager.data;

import io.sl4sh.xmanager.economy.XPlayerAccount;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XAccountContainer {

    @Setting(value = "playerAccounts")
    private List<XPlayerAccount> playerAccounts = new ArrayList<>();

    public XAccountContainer() {

    }

    public List<XPlayerAccount> getPlayerAccounts() {
        return playerAccounts;
    }

    public void setPlayerAccounts(List<XPlayerAccount> playerAccounts) {
        this.playerAccounts = playerAccounts;
    }

    public void addAccount(XPlayerAccount account){

        this.playerAccounts.add(account);

    }

}
