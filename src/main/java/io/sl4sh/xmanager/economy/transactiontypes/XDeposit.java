package io.sl4sh.xmanager.economy.transactiontypes;

import org.spongepowered.api.service.economy.transaction.TransactionType;

public class XDeposit implements TransactionType {
    @Override
    public String getId() {
        return "buy";
    }

    @Override
    public String getName() {
        return "Buy";
    }
}
