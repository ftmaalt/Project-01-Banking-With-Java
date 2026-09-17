package com.ga.acmebank.cards;

public class MasterCard extends Card{
    public MasterCard(){
    withdraw_limit=5000;
    transfer_limit=20_000;
    transferDifAccount_limit=10_000;
    deposit_limit=100_000;
    depositDifAccount_limit=200_000;
    }


}
