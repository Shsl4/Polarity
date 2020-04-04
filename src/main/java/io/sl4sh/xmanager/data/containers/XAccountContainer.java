package io.sl4sh.xmanager.data.containers;

import io.sl4sh.xmanager.economy.XAccount;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XAccountContainer {

    @Setting(value = "accounts")
    private List<XAccount> accounts = new ArrayList<>();

    public XAccountContainer() {

    }

    public List<XAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<XAccount> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(XAccount account){

        this.accounts.add(account);

    }

}
