package com.ga.acmebank.cards;

public class MasterCardTitanium extends Card{
    @Override
    public void setWithdraw_limit(double withdraw_limit) {
        super.setWithdraw_limit(10-000);
    }

    @Override
    public void setTransfer_limit(double transfer_limit) {
        super.setTransfer_limit(20-000);
    }

    @Override
    public void setTransferDifAccount_limit(double transferDifAccount_limit) {
        super.setTransferDifAccount_limit(40-000);
    }

    @Override
    public void setDeposit_limit(double deposit_limit) {
        super.setDeposit_limit(100-000);
    }

    @Override
    public void setDepositDifAccount_limit(double depositDifAccount_limit) {
        super.setDepositDifAccount_limit(200-000);
    }


}
