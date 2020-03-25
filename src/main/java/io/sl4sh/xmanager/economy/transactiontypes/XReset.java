package io.sl4sh.xmanager.economy.transactiontypes;

import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

public class XReset implements TransactionType {
    @Override
    public String getId() {
        return "reset";
    }

    @Override
    public String getName() {
        return "Reset";
    }
}
