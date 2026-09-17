package com.ga.acmebank.cards;

public class MasterCardPlatinum extends Card{
    public MasterCardPlatinum() {
        withdraw_limit=20_000;
        transfer_limit=80_000;
        transferDifAccount_limit=40_000;
        deposit_limit=100_000;
        depositDifAccount_limit=200_000;
    }


}
