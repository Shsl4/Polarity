package io.sl4sh.xmanager.data;

import io.sl4sh.xmanager.economy.XPlayerAccount;

import java.util.ArrayList;
import java.util.List;

public class XAccountContainer {

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
