package com.ga.acmebank.account;

public class SavingsAccount implements BankAccount{
    public SavingsAccount(String userID) {
    }

    @Override
    public boolean hasSavingsAccount() {
        return false;
    }
    @Override
    public String UpdateCardType() {
        return "";
    }

    @Override
    public String depositMoney(double balance) {
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

    @Override
    public String transferMoney(double balance) {
        return "";
    }

    @Override
    public String transferToAccount(double balance, String userID) {
        return "";
    }
}
