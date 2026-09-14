package com.ga.acmebank.cards;

public class MasterCard extends Card{
    @Override
    public void setWithdraw_limit(double withdraw_limit) {
        super.setWithdraw_limit(5000);
    }

    @Override
    public void setTransfer_limit(double transfer_limit) {
        super.setTransfer_limit(10_000);
    }

    @Override
    public void setTransferDifAccount_limit(double transferDifAccount_limit) {
        super.setTransferDifAccount_limit(20_000);
    }

    @Override
    public void setDeposit_limit(double deposit_limit) {
        super.setDeposit_limit(100_000);
    }

    @Override
    public void setDepositDifAccount_limit(double depositDifAccount_limit) {
        super.setDepositDifAccount_limit(200_000);
    }

}
