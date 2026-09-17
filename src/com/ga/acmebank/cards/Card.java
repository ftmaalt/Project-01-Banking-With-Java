package com.ga.acmebank.cards;

public class Card {
    double withdraw_limit = 0.0;
    double transfer_limit = 0.0;
    double transferDifAccount_limit = 0.0;
    double deposit_limit = 0.0;
    double depositDifAccount_limit = 0.0;

    public double getDeposit_limit() {
        return deposit_limit;
    }

    public double getDepositDifAccount_limit() {
        return depositDifAccount_limit;
    }

    public double getTransfer_limit() {
        return transfer_limit;
    }

    public double getTransferDifAccount_limit() {
        return transferDifAccount_limit;
    }

    public double getWithdraw_limit() {
        return withdraw_limit;
    }

}
