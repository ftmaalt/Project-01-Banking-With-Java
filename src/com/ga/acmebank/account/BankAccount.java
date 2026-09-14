package com.ga.acmebank.account;

import java.io.IOException;

public interface BankAccount {
    String defaultcard= "MasterCard";
    double getActBalance(String userID, String actType) throws IOException;
    void updateCurrentBalance(String userID, String actType);
    String savingAccountID(String userID);
    String checkingAccountID(String userID);
    String UpdateCardType();
    String depositMoney(double balance);
    String withdrawMoney(double balance);
    String depositToAccount(double balance, String userID);
    String transferMoney(double balance,String otherAccountID );
    String transferToAccount(double balance, String userID);

}
