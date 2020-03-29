package io.sl4sh.xmanager.economy.transactiontypes;

import org.spongepowered.api.service.economy.transaction.TransactionType;

public class XWithdraw implements TransactionType {
    @Override
    public String getId() {
        return "withdraw";
    }

    @Override
    public String getName() {
        return "Withdraw";
    }
}
