package io.sl4sh.xmanager.economy.transactiontypes;

import org.spongepowered.api.service.economy.transaction.TransactionType;

public enum XTransactionTypes  {

    Deposit(new XDeposit()),
    Transfer(new XTransfer()),
    Withdraw(new XWithdraw()),
    Reset(new XReset());

    private TransactionType transactionType;

    XTransactionTypes(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionType getTransactionType(){

        return transactionType;

    }

}
