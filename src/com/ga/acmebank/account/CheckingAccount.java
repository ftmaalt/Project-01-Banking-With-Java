package com.ga.acmebank.account;

public class CheckingAccount implements BankAccount {
    @Override
    public boolean hasSavingsAccount() {
        return false;
    }
//    public CheckingAccount() {
//        super();
//    }
    public CheckingAccount(String userID) {
    }

    @Override
    public String UpdateCardType() {

        //gets user balance, if balance is greater than 10000, then upgrade card to titanium,
        // if card is titanium and balance is greater than 50-000 and user has deposited more than 700000 accumalted balance in account then upgrade to platinum
        return "";
    }

    @Override
    public String depositMoney(double balance) {
        //gets balance from somewhere

        return "Sorry Try Again";
    }



    @Override
    public String transferToAccount(double balance, String userID) {
        return "";
    }

    @Override
    public String transferMoney(double balance) {
        return "";
    }

    @Override
    public String depositToAccount(double balance, String userID) {
        return "";
    }

    @Override
    public String withdrawMoney(double balance) {
        return "";
    }
}
