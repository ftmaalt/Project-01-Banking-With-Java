package com.ga.acmebank.account;

public interface BankAccount {
    String defaultcard= "MasterCard";
    boolean hasSavingsAccount();
    String UpdateCardType();
    String depositMoney(double balance);
    String withdrawMoney(double balance);
    String depositToAccount(double balance, String userID);
    String transferMoney(double balance);
    String transferToAccount(double balance, String userID);

}
