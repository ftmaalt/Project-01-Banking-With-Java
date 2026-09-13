package com.ga.acmebank.cards;

public class Card {
    static double withdraw_limit=0.0;
    static double transfer_limit=0.0;
    static double transferDifAccount_limit =0.0;
    static double deposit_limit=0.0;
    static double depositDifAccount_limit =0.0;

    public static double getWithdraw_limit() {
        return withdraw_limit;
    }

    public void setWithdraw_limit(double withdraw_limit) {
        Card.withdraw_limit = withdraw_limit;
    }

    public double getDepositDifAccount_limit() {
        return depositDifAccount_limit;
    }

    public void setDepositDifAccount_limit(double depositDifAccount_limit) {
        Card.depositDifAccount_limit = depositDifAccount_limit;
    }

    public double getDeposit_limit() {
        return deposit_limit;
    }

    public void setDeposit_limit(double deposit_limit) {
        Card.deposit_limit = deposit_limit;
    }

    public double getTransferDifAccount_limit() {
        return transferDifAccount_limit;
    }

    public void setTransferDifAccount_limit(double transferDifAccount_limit) {
        Card.transferDifAccount_limit = transferDifAccount_limit;
    }

    public double getTransfer_limit() {
        return transfer_limit;
    }

    public void setTransfer_limit(double transfer_limit) {
        Card.transfer_limit = transfer_limit;
    }
}
