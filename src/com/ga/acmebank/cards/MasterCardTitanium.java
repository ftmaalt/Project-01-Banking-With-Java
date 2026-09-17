package com.ga.acmebank.cards;

public class MasterCardTitanium extends Card{
    public MasterCardTitanium() {
        withdraw_limit=10_000;
        transfer_limit=40_000;
        transferDifAccount_limit=20_000;
        deposit_limit=100_000;
        depositDifAccount_limit=200_000;
    }

}

