package io.sl4sh.xmanager.economy.transactiontypes;

import org.spongepowered.api.service.economy.transaction.TransactionType;

public class XTransfer implements TransactionType {
    @Override
    public String getId() {
        return "transfer";
    }

    @Override
    public String getName() {
        return "Transfer";
    }
}
