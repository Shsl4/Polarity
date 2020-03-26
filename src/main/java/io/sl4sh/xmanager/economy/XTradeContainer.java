package io.sl4sh.xmanager.economy;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class XTradeContainer {

    @Setting(value = "tradeList")
    private List<XTradeBuilder> tradeList = new ArrayList<>();

    public XTradeContainer() {}

    public XTradeContainer(List<XTradeBuilder> tradeList) {
        this.tradeList = tradeList;
    }

    public List<XTradeBuilder> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<XTradeBuilder> tradeList) {
        this.tradeList = tradeList;
    }

}
